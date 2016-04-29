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

public class AstBindHandler extends BaseHttpHandler {

	String username;
	private String mobile;
	String gameId;

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 * @param udid
	 * @param source
	 * @param gid
	 */
	public AstBindHandler(boolean debugMode, HttpCallback callback,
			String mobile, String gameId, String username) {
		super(debugMode, callback);
		this.mobile = mobile;
		this.gameId = gameId;
		this.username = username;
	}

	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		long ts = System.currentTimeMillis();
		setParam("mobile", mobile);
		setParam("gameId", gameId);
		setParam("username", username);
		setParam("ts", ts + "");
		String ticket = GameUtil.getAstBindCode(username, mobile, ts + "");
		setParam("ticket", ticket);
	}

	/**
	 * { "state":1, "code":1, "datas":{ "username":" 3nt7nr2",// 成功注册的用户名
	 * "yxSource":"aoshitang", // 用户来源 "userId":"110891", // 对应生成的用户唯一标识
	 * "mobile":"133*****711",//玩家绑定手机号，没绑定时为NULL
	 * "alterable":true,//用户名可修改，false为不可修改 "mobileBind":1, // 0：未绑定，1：绑定手机
	 * "accessToken":"42abdfd11d6a6e7d4e9462b61c3cf833" // 普通用户访问令牌 } }
	 */
	public ResponseEntity parseData(String content) {
		/**
		 * 成功注册 { "state":1, "code":1, "datas":{ "username":" 3nt7nr2", //
		 * 成功注册的用户名 "yxSource":"aoshitang", // 用户来源 "userId":"110891", //
		 * 对应生成的用户唯一标识 "mobile":"133*****711",//玩家绑定手机号，没绑定时为NULL
		 * "alterable":true, //用户名是否可修改，true为可修改 "mobileBind":0, // 0：未绑定，1：绑定手机
		 * "accessToken":"42abdfd11d6a6e7d4e9462b61c3cf833" // 普通用户访问令牌 } }
		 */
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
		return "http://testos.mobile.aoshitang.com/v2/astBindMobile.action";
	}

	public String getReleaseUrl() {
		return "http://mob.aoshitang.com/v2/astBindMobile.action";
	}

	public String logTag() {
		return "AstBindHandler";
	}
}
