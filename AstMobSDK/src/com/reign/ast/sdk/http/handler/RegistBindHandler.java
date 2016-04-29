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

public class RegistBindHandler extends BaseHttpHandler {

	String tmpPlayerId;
	String password;
	String username;

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 * @param udid
	 * @param source
	 * @param gid
	 */
	public RegistBindHandler(boolean debugMode, HttpCallback callback,
			String tmpPlayerId, String username, String password) {
		super(debugMode, callback);
		this.username = username;
		this.tmpPlayerId = tmpPlayerId;
		this.password = password;
	}

	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		long ts = System.currentTimeMillis();
		String openudid = DeviceUtil.openUdid;
		setParam("mac", "");
		setParam("idfa", "");
		setParam("openudid", openudid);
		setParam("bindType", "ast_bind_tmp");
		setParam("tmpPlayerId", tmpPlayerId);
		setParam("username", username);
		setParam("password", password);
		setParam("gameId", String.valueOf(AstGamePlatform.getInstance()
				.getAppInfo().gameId));
		setParam("ts", ts + "");
		String ticket = GameUtil
				.getLoginBindSig("", "", openudid, "ast_bind_tmp", tmpPlayerId,
						username, password, String.valueOf(AstGamePlatform
								.getInstance().getAppInfo().gameId), ts + "");
		setParam("ticket", ticket);
	}

	/**
	 * { "state":1, "code":1, "datas":{ "username":"FOfPbHWXqpoxeq", //傲世堂用户名
	 * "yxSource":"aoshitang", // 用户来源 "userId":"110891", // 对应生成的用户唯一标识
	 * "accessToken":"42abdfd11d6a6e7d4e9462b61c3cf833",// 普通用户访问令牌
	 * "alterable":false,//用户名不可修改，true为可修改 "mobileBind":0, // 0：未绑定，1：绑定手机
	 * "mobile":"183*****706" //用户绑定手机号,未绑定时为NULL } }
	 */
	public ResponseEntity parseData(String content) {
		ResponseEntity entity = new ResponseEntity();
		int state = 0, code = 0;
		String msg = "";
		long ts = 0l;
		String accessToken = null, username = null, tmpPlayerId = null, userId = null;
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
						accessToken = jdatas.getString("accessToken");
					}
					if (!jdatas.isNull("tmpPlayerId")) {
						tmpPlayerId = jdatas.getString("tmpPlayerId");
					}
					if (!jdatas.isNull("username")) {
						username = jdatas.getString("username");
					}
					if (!jdatas.isNull("userId")) {
						userId = jdatas.getString("userId");
					}
					if (!jdatas.isNull("ts")) {
						ts = jdatas.getLong("ts");
					}
					Log.d(logTag(), "token: " + accessToken + ", tmpPlayerId: "
							+ tmpPlayerId + ", userId: " + userId + ", ts: "
							+ ts);
					UserInfo userInfo = new UserInfo();
					userInfo.setUserId(userId);
					userInfo.setToken(accessToken);
					userInfo.setQuickUser(false);
					userInfo.setUserName(username);
					userInfo.setSource(AstGamePlatform.getInstance()
							.getSource());
					userInfo.setTmpPlayerId(tmpPlayerId);
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
		return "http://testos.mobile.aoshitang.com/v2/regBind.action";
	}

	public String getReleaseUrl() {
		return "http://mob.aoshitang.com/v2/regBind.action";
	}

	public String logTag() {
		return "RegistBindHandler";
	}
}
