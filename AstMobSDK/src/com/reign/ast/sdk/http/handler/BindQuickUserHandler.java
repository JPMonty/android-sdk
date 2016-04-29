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
import com.reign.ast.sdk.util.GameUtil;

/**
 * 绑定临时账号
 * @author zhouwenjia
 *
 */
public class BindQuickUserHandler extends BaseHttpHandler {

	private static final String TAG = BindQuickUserHandler.class.getSimpleName();
	
	/** tmpPlayerId */
	private String tmpPlayerId;
	/**  */
	private String username;
	/**  */
	private String password;
	
	/**
	 * 构造函数
	 * @param debugMode
	 * @param callback
	 * @param gameId
	 * @param source
	 * @param subSource
	 */
	public BindQuickUserHandler(boolean debugMode, HttpCallback callback, String tmpPlayerId, String username, String password) {
		super(debugMode, callback);
		this.tmpPlayerId = tmpPlayerId;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * 准备数据
	 */
	@Override
	public void prepareRequestOther() {
		
		long ts = System.currentTimeMillis();
		setParam("tmpPlayerId", tmpPlayerId);
		setParam("username", username);
		setParam("password", password);
		setParam("ts", ts + "");
		
		String gameId = String.valueOf(AstGamePlatform.getInstance().getAppInfo().gameId);
		setParam("gameId", gameId);
		
		String ticket = GameUtil.getBindQuickUserSig(ts, tmpPlayerId, username, password);
		setParam("ticket", ticket);
	}
	
	/**
	 * 处理返回
	 */
	@Override
	public ResponseEntity parseData(String content) {
		ResponseEntity entity = new ResponseEntity();
		int state = 0, code = 0;
		String msg = "";
		long ts = 0l;
		String token = null, retUserName = null, userId = null;
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
					if (!jdatas.isNull("ts")) {
						ts = jdatas.getLong("ts");
					}
					if (!jdatas.isNull("token")) {
						token = jdatas.getString("token");
					}
					if (!jdatas.isNull("username")) {
						retUserName = jdatas.getString("username");
					}
					if (!jdatas.isNull("userId")) {
						userId = jdatas.getString("userId");
					}
					
					Log.d(logTag(), "token: " + token + ", tmpPlayerId: " + tmpPlayerId + ", userId: " + userId + ", ts: " + ts);
					UserInfo userInfo = new UserInfo();
					userInfo.setUserId(userId);
					userInfo.setUserName(retUserName);
					userInfo.setToken(token);;
					userInfo.setQuickUser(false);
					userInfo.setSource(AstGamePlatform.getInstance().getSource());
					entity.setData(userInfo);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			entity.setCode(ErrorCodeTransfer.ERROR_PARSE_DATA);
			entity.setMsg(ErrorCodeTransfer.getErrorMsg(ErrorCodeTransfer.ERROR_PARSE_DATA));
			return entity;
		}
		entity.setCode(state);
		entity.setMsg(msg);
		return entity;
	}
	
	@Override
	public String getDebugUrl() {
		return "http://testos.mobile.aoshitang.com/v2/v2/bindTmpPlayer.action";
	}
	
	@Override
	public String getReleaseUrl() {
		return "http://m.zz2mob.aoshitang.com/bindTmpPlayer.action";
	}
	
	@Override
	public String logTag() {
		return TAG;
	}
	
}
