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

/**
 * 注册处理
 * 
 * @author zhouwenjia
 * 
 */
public class RegHandler extends BaseHttpHandler {
	private String gameId;
	private String userName;
	private String password;

	private String tmpPlayerId;

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 * @param userName
	 * @param password
	 * @param gameId
	 */
	public RegHandler(boolean debugMode, HttpCallback callback,
			String userName, String password, String gameId, String tmpPlayerId) {
		super(debugMode, callback);

		this.gameId = gameId;
		this.userName = userName;
		this.password = password;
		this.tmpPlayerId = tmpPlayerId;
	}

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 * @param userName
	 * @param password
	 * @param gameId
	 */
	public RegHandler(boolean debugMode, HttpCallback callback,
			String userName, String password, String gameId) {
		super(debugMode, callback);

		this.gameId = gameId;
		this.userName = userName;
		this.password = password;
		this.tmpPlayerId = null;
	}

	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		long ts = System.currentTimeMillis();
		setParam("mac", "");
		setParam("idfa", "");
		setParam("openudid", DeviceUtil.openUdid);
		setParam("gameId", gameId);
		setParam("username", userName);
		setParam("password", password);
		setParam("ts", ts + "");

		String ticket = GameUtil.getRegTicket("", "", DeviceUtil.openUdid,
				gameId, userName, password, ts + "");

		setParam("ticket", ticket);
	}

	/**
	 * { "state":1, "code":1, "datas":{ "username":"FOfPbHWXqpoxeq", // 成功注册的用户名
	 * "yxSource":"aoshitang", // 用户来源 "userId":"110891", // 对应生成的用户唯一标识
	 * "accessToken":"42abdfd11d6a6e7d4e9462b61c3cf833" // 普通用户访问令牌 } } * 处理结果返回
	 */
	public ResponseEntity parseData(String content) {
		// {"state":0,"code":308,"datas":{"message":"用户名格式错误（6-20位数字、英文、下划线）"}}
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
				if (!jsonObj.isNull("datas")) {
					JSONObject jdatas = jsonObj.getJSONObject("datas");
					if (!jdatas.isNull("message")) {
						msg = jdatas.getString("message");
					}
				}
				// msg = ErrorCodeTransfer.getErrorMsg(code);
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
							+ retUserName + ", userId: " + userId + ", ts: "
							+ ts);
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
		
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setUserName(retUserName);
		userInfo.setToken(token);
		userInfo.setQuickUser(false);
		userInfo.setSource(AstGamePlatform.getInstance()
				.getSource());
		entity.setData(userInfo);
		return entity;
	}

	public String getDebugUrl() {
		return "http://testos.mobile.aoshitang.com/v2/commonRegist.action";
	}

	public String getReleaseUrl() {
		return "http://mob.aoshitang.com/v2/commonRegist.action";
	}

	public String logTag() {
		return "RegHandler";
	}
}
