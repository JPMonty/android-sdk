package com.reign.ast.sdk.http.handler;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.ErrorCodeTransfer;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.DeviceUtil;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

public class QuickLoginHandler extends BaseHttpHandler {
	private static final String TAG = QuickLoginHandler.class.getSimpleName();

	private String accessToken;
	private String gameId;

	public QuickLoginHandler(boolean debugMode, HttpCallback callback,
			String accessToken, String gameId) {
		super(debugMode, callback);
		this.accessToken = accessToken;
		this.gameId = gameId;
		Logger.d(TAG, "QuickLoginHandler: accessToken:" + accessToken);
	}

	@Override
	public void prepareRequestOther() {

		long ts = System.currentTimeMillis();
		setParam("mac", "");
		setParam("idfa", "");
		setParam("openudid", DeviceUtil.openUdid);
		
		setParam("gameId", gameId);
		
		setParam("accessToken", accessToken);
		
		setParam("ts", ts + "");
		String ticket = GameUtil.getQuickLoginSig("", "", DeviceUtil.openUdid,
				gameId, accessToken, ts + "");
		setParam("ticket", ticket);
	}

	/*
	 * 
	 * 成功登录 { "state":1, "code":1, "datas":{ "isBinded":0, // 0：未绑定，1：绑定
	 * "tmpPlayerId":"BA7B6165044C6E779B57487BC7EE2941@temp",
	 * "yxSource":"aoshitang", "userId":110888, // 注册成功生成的用户唯一标识
	 * "lastServerFlag":"",// 最近登录服务器标识
	 * "accessToken":"69158fc69f09ecb797f47439c6221ac6"// 访问令牌 } }
	 * 
	 * @see com.reign.ast.sdk.http.BaseHttpHandler#parseData(java.lang.String)
	 */
	@Override
	public ResponseEntity parseData(String content) {

		ResponseEntity entity = new ResponseEntity();
		int state = 0, code = 0;
		String msg = "";
		long ts = 0l;
		String accessToken = null, tmpPlayerId = null, userId = null;
		try {
			JSONObject jsonObj = new JSONObject(content);
			if (!jsonObj.isNull("state")) {
				state = jsonObj.getInt("state");
			}
			if (!jsonObj.isNull("code")) {
				code = jsonObj.getInt("code");
			}
			if (0 == state) {
				// 失败
				msg = ErrorCodeTransfer.getErrorMsg(code);
			} else {
				// 成功
				if (!jsonObj.isNull("datas")) {
					JSONObject jdatas = jsonObj.getJSONObject("datas");
					if (!jdatas.isNull("accessToken")) {
						accessToken = jdatas.getString("accessToken");
					}
					if (!jdatas.isNull("ts")) {
						ts = jdatas.getLong("ts");
					}
					if (!jdatas.isNull("tmpPlayerId")) {
						tmpPlayerId = jdatas.getString("tmpPlayerId");
					}
					if (!jdatas.isNull("userId")) {
						userId = jdatas.getString("userId");
					}
					Log.d(logTag(), "accessToken: " + accessToken + "tmpPlayerId:" + tmpPlayerId  + "userId:" + userId);
					UserInfo userInfo = new UserInfo();
					userInfo.setTmpPlayerId(tmpPlayerId);
					userInfo.setUserId(userId);
					userInfo.setToken(accessToken);
					userInfo.setQuickUser(true);
					userInfo.setSource(AstGamePlatform.getInstance()
							.getSource());
					entity.setData(userInfo);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			entity.setCode(ErrorCodeTransfer.ERROR_PARSE_DATA);
			entity.setMsg(ErrorCodeTransfer
					.getErrorMsg(ErrorCodeTransfer.ERROR_PARSE_DATA));
			return entity;
		}

		entity.setCode(code);
		entity.setMsg(msg);
		return entity;
	}

	@Override
	public String getDebugUrl() {
		// TODO Auto-generated method stub
		return "http://testos.mobile.aoshitang.com/v2/quickLogin.action";
	}

	@Override
	public String getReleaseUrl() {
		// TODO Auto-generated method stub
		return "http://mob.aoshitang.com/v2/quickLogin.action";
	}

	@Override
	public String logTag() {
		// TODO Auto-generated method stub
		return TAG;
	}

}
