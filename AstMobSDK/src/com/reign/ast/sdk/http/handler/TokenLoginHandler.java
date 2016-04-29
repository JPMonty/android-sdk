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

/**
 * 登陆处理
 * 
 * @author zhouwenjia
 * 
 */
public class TokenLoginHandler extends BaseHttpHandler {
	private static final String TAG = TokenLoginHandler.class.getSimpleName();
	private String gameId;
	private String userName;
	private String accessToken;

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 * @param userName
	 * @param password
	 * @param gameId
	 */
	public TokenLoginHandler(boolean debugMode, HttpCallback callback,
			String userName, String accessToken, String gameId) {
		super(debugMode, callback);
		this.userName = userName;
		this.accessToken = accessToken;
		this.gameId = gameId;
		Logger.d(TAG, "TokenLoginHandler: User:" + userName + " accessToken:"
				+ accessToken + " gameId:" + gameId);
	}

	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		long ts = System.currentTimeMillis();
		setParam("mac", "");
		setParam("idfa", "");
		setParam("openudid", DeviceUtil.openUdid);

		setParam("username", userName);
		setParam("gameId", gameId);
		setParam("accessToken", accessToken);

		setParam("ts", ts + "");
		String ticket = GameUtil.getTokenLoginSig("", "", DeviceUtil.openUdid,
				userName, gameId, accessToken, ts + "");
		setParam("ticket", ticket);
	}

	/**
	 * 成功登录 { "state":1, "code":1, "datas":{ "username":"FOfPbHWXqpoxeq", //
	 * 成功注册的用户名 "yxSource":"aoshitang", // 用户来源 "userId":"110891", //
	 * 对应生成的用户唯一标识 "accessToken":"42abdfd11d6a6e7d4e9462b61c3cf833" // 普通用户访问令牌
	 * } }
	 * 
	 * 
	 * 处理结果返回
	 */
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
				msg = jsonObj.getJSONObject("datas").getString("message");
			} else {
				// 成功
				if (!jsonObj.isNull("datas")) {
					JSONObject jdatas = jsonObj.getJSONObject("datas");
					if (!jdatas.isNull("accessToken")) {
						token = jdatas.getString("accessToken");
					}
					if (!jdatas.isNull("username")) {
						retUserName = jdatas.getString("username");
					}
					if (!jdatas.isNull("userId")) {
						userId = jdatas.getString("userId");
					}
					if (!jdatas.isNull("ts")) {
						ts = jdatas.getLong("ts");
					}
					Log.d(logTag(), "token: " + token + ", retUserName: "
							+ retUserName + ", userId: " + userId);
					UserInfo userInfo = new UserInfo();
					userInfo.setUserId(userId);
					userInfo.setUserName(retUserName);
					userInfo.setToken(token);
					;
					userInfo.setQuickUser(false);
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

	public String getDebugUrl() {
		return "http://testos.mobile.aoshitang.com/v2/astTokenLogin.action";
	}

	public String getReleaseUrl() {
		return "http://mob.aoshitang.com/v2/astTokenLogin.action";
	}

	public String logTag() {
		return TAG;
	}
}
