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
 * 快速注册
 * 
 * @author zhouwenjia
 * 
 */
public class QuickRegHandler extends BaseHttpHandler {

	private String gameId;

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 * @param udid
	 * @param source
	 * @param gid
	 */
	public QuickRegHandler(boolean debugMode, HttpCallback callback,
			String gameId) {
		super(debugMode, callback);
		this.gameId = gameId;
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
		setParam("gameId", gameId);
		setParam("ts", ts + "");

		String ticket = GameUtil.getQuickRegSig("", "", openudid, gameId, ts);
		setParam("ticket", ticket);
	}

	/**
	 *
	 * 处理结果返回
	 */
	public ResponseEntity parseData(String content) {

		ResponseEntity entity = new ResponseEntity();
		int state = 0, code = 0;
		String msg = "";
		long ts = 0l;
		int isBinded = 0;
		String accessToken = null, username = null, mobile = null, tmpPlayerId = null, userId = null;
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
					if (!jdatas.isNull("tmpPlayerId")) {
						tmpPlayerId = jdatas.getString("tmpPlayerId");
					}
					if (!jdatas.isNull("userId")) {
						userId = jdatas.getString("userId");
					}
					if (!jdatas.isNull("username")) {
						username = jdatas.getString("username");
					}
					if (!jdatas.isNull("ts")) {
						ts = jdatas.getLong("ts");
					}
					if (!jdatas.isNull("isBinded")) {
						isBinded = jdatas.getInt("isBinded");
					}
					if (!jdatas.isNull("mobile")) {
						mobile = jdatas.getString("mobile");
					}
					Log.d(logTag(), "token: " + accessToken + ", tmpPlayerId: "
							+ tmpPlayerId + ", userId: " + userId + ", ts: "
							+ ts);
					UserInfo userInfo = new UserInfo();
					userInfo.setUserId(userId);
					userInfo.setUserName(username);
					userInfo.setToken(accessToken);
					userInfo.setMobile(mobile);
					userInfo.setQuickUser(isBinded == 0);
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
		return "http://testos.mobile.aoshitang.com/v2/quickROL.action";
	}

	public String getReleaseUrl() {
		return "http://mob.aoshitang.com/v2/quickROL.action";
	}

	public String logTag() {
		return "QuickRegHandler";
	}
}
