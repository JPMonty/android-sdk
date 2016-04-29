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
import com.ta.utdid2.android.utils.StringUtils;

/**
 * 发送验证码处理
 * 
 * @author zhouwenjia
 * 
 */
public class ResetPasswordHandler extends BaseHttpHandler {
	private String mobile;
	private String password;
	private String key;
	private String gameId;

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 * @param udid
	 * @param source
	 * @param gid
	 * @param mobile
	 */
	public ResetPasswordHandler(boolean debugMode, HttpCallback callback,
			String mobile, String password, String key, String gameId) {
		super(debugMode, callback);
		this.mobile = mobile;
		this.password = password;
		this.key = key;
		this.gameId = gameId;
	}

	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		long ts = System.currentTimeMillis();
		setParam("mobile", mobile);
		setParam("password", password);
		setParam("gameId", gameId);
		setParam("key", key);
		setParam("ts", ts + "");
		String ticket = GameUtil.getResetPasswordSig(mobile, password, gameId,
				key, ts + "");
		setParam("ticket", ticket);
	}

	/**
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
				msg = ErrorCodeTransfer.getErrorMsg(code);
				if (StringUtils.isEmpty(msg)) {
					msg = jsonObj.getJSONObject("datas").getString("message");
				}
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
		return "http://testos.mobile.aoshitang.com/v2/resetPassword.action";
	}

	public String getReleaseUrl() {
		return "http://mob.aoshitang.com/v2/resetPassword.action";
	}

	public String logTag() {
		return "ResetPasswordHandler";
	}
}
