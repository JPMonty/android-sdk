package com.reign.ast.sdk.http.handler;

import org.json.JSONException;
import org.json.JSONObject;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.ErrorCodeTransfer;
import com.reign.ast.sdk.util.GameUtil;

public class GuestPhoneCanBindHandler extends BaseHttpHandler {
	private String mobile;

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
	public GuestPhoneCanBindHandler(boolean debugMode, HttpCallback callback,
			String mobile) {
		super(debugMode, callback);
		this.mobile = mobile;
	}

	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		long ts = System.currentTimeMillis();
		setParam("mobile", mobile);
		setParam("ts", ts + "");
		String ticket = GameUtil.getGuestBindPhoneSig(mobile, ts + "");
		setParam("ticket", ticket);
	}

	/**
	 * { "state":1, "code":1, "datas":{ "message":"该手机号可绑定" } }
	 * 
	 * {"state":0,"code":317,"datas":{"message":"手机号已被绑定."}
	 */
	public ResponseEntity parseData(String content) {
		ResponseEntity entity = new ResponseEntity();
		int state = 0, code = 0;
		String msg = "";
		long ts = 0l;
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
		return "http://testos.mobile.aoshitang.com/v2/numberCanBind.action";
	}

	public String getReleaseUrl() {
		return "http://mob.aoshitang.com/v2/numberCanBind.action";
	}

	public String logTag() {
		return "PhoneCanBindHandler";
	}
}