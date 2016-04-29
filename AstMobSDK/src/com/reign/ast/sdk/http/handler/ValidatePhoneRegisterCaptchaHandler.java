package com.reign.ast.sdk.http.handler;

import org.json.JSONException;
import org.json.JSONObject;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.ErrorCodeTransfer;
import com.reign.ast.sdk.util.GameUtil;

public class ValidatePhoneRegisterCaptchaHandler extends BaseHttpHandler {

	private String phoneNumber;
	private String code;

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 * @param udid
	 * @param source
	 * @param gid
	 */
	public ValidatePhoneRegisterCaptchaHandler(boolean debugMode,
			HttpCallback callback, String gameId, String phoneNumber,
			String code) {
		super(debugMode, callback);
		this.phoneNumber = phoneNumber;
		this.code = code;
	}

	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		long ts = System.currentTimeMillis();
		setParam("code", code);
		setParam("mobile", phoneNumber);
		setParam("ts", ts + "");
		String ticket = GameUtil.getValidateRegistCode(code, phoneNumber, ts
				+ "");
		setParam("ticket", ticket);
	}

	/**
	 * 处理结果返回
	 */
	public ResponseEntity parseData(String content) {

		ResponseEntity entity = new ResponseEntity();
		int state = 0, code = 0;
		String msg = "";
		long ts = 0l;
		int isBinded = 0;
		String accessToken = null, tmpPlayerId = null, userId = null;
		try {
			JSONObject jsonObj = new JSONObject(content);
			if (!jsonObj.isNull("state")) {
				state = jsonObj.getInt("state");
			}
			if (!jsonObj.isNull("code")) {
				code = jsonObj.getInt("code");
			}
			msg = jsonObj.getJSONObject("datas").getString("message");
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
		return "http://testos.mobile.aoshitang.com/v2/validateRegistCode.action";
	}

	public String getReleaseUrl() {
		return "http://mob.aoshitang.com/v2/validateRegistCode.action";
	}

	public String logTag() {
		return "ValidatePhoneRegisterCaptchaHandler";
	}
}
