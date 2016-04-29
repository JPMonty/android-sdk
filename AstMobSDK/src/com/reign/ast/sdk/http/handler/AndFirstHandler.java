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
 * android广告激活(包内未包含来源信息的激活接口)，android游戏应用首次启动，发送激活请求
 * @author zhouwenjia
 *
 */
public class AndFirstHandler extends BaseHttpHandler {
	
	private static final String TAG = AndFirstHandler.class.getSimpleName();
	
	/** gameid */
	private String gameId;
	
	/**
	 * 构造函数
	 * @param debugMode
	 * @param callback
	 * @param gameId
	 */
	public AndFirstHandler(boolean debugMode, HttpCallback callback, String gameId) {
		super(debugMode, callback);
		this.gameId = gameId;
	}
	
	/**
	 * 准备数据
	 */
	@Override
	public void prepareRequestOther() {
		String openudid = DeviceUtil.openUdid;
		
		setParam("mac", "");
		setParam("idfa", "");
		setParam("openudid", openudid);
		setParam("gameId", gameId);
		
		String token = GameUtil.getAndFirstSig("", "", openudid, gameId);
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
		return "http://testos.mobile.aoshitang.com/v2/v2/v2/andFirst.action";
	}
	
	@Override
	public String getReleaseUrl() {
		return "http://m.zz2mob.aoshitang.com/v2/andFirst.action";
	}
	
	@Override
	public String logTag() {
		return TAG;
	}
}
