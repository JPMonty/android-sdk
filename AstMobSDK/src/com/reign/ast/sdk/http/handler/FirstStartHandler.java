package com.reign.ast.sdk.http.handler;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.ErrorCodeTransfer;
import com.reign.ast.sdk.util.DeviceUtil;
import com.reign.ast.sdk.util.GameUtil;

/**
 * 首次打开app调用接口
 * @author zhouwenjia
 *
 */
public class FirstStartHandler extends BaseHttpHandler {
	
	private static final String TAG = AdActiveHandler.class.getSimpleName();
	
	/** gameid */
	private String gameId;
	
	/**
	 * 构造函数
	 * @param debugMode
	 * @param callback
	 * @param gameId
	 * @param source
	 * @param subSource
	 */
	public FirstStartHandler(boolean debugMode, HttpCallback callback, String gameId) {
		super(debugMode, callback);
		this.gameId = gameId;
	}
	
	/**
	 * 准备数据
	 */
	@Override
	public void prepareRequestOther() {
		
		String openudid = DeviceUtil.openUdid;
		String version = DeviceUtil.getDeviceVersion();
		String device = DeviceUtil.getDeviceType();
		setParam("mac", "");
		setParam("idfa", "");
		setParam("openudid", openudid);
		setParam("gameId", gameId);
		setParam("version", version);
		setParam("device", device);
		
		String token = GameUtil.getFirstStartSig("", "", openudid, gameId);
		setParam("token", token);
	}
	
	/**
	 * 处理返回
	 */
	@Override
	public ResponseEntity parseData(String content) {
		ResponseEntity entity = new ResponseEntity();
		int state = 0;
		String msg = "";
		try {
			JSONObject jsonObj = new JSONObject(content);
			if (!jsonObj.isNull("state")) {
				state = jsonObj.getInt("state");
			}
			msg = state + "";
			Log.d(TAG, "state: " + state);
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
		return "http://testos.mobile.aoshitang.com/v2/v2/firstStart.action";
	}
	
	@Override
	public String getReleaseUrl() {
		return "http://m.zz2mob.aoshitang.com/firstStart.action";
	}
	
	@Override
	public String logTag() {
		return TAG;
	}
}