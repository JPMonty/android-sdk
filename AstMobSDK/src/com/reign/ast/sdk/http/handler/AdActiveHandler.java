package com.reign.ast.sdk.http.handler;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.ErrorCodeTransfer;
import com.reign.ast.sdk.util.DeviceUtil;

/**
 * 广告激活
 * @author zhouwenjia
 *
 */
public class AdActiveHandler extends BaseHttpHandler {
	
	private static final String TAG = AdActiveHandler.class.getSimpleName();
	
	/** gameid */
	private String gameId;
	/**  */
	private String source;
	/**  */
	private String subSource;
	
	/**
	 * 构造函数
	 * @param debugMode
	 * @param callback
	 * @param gameId
	 * @param source
	 * @param subSource
	 */
	public AdActiveHandler(boolean debugMode, HttpCallback callback, String gameId, String source, String subSource) {
		super(debugMode, callback);
		this.gameId = gameId;
		this.source = source;
		this.subSource = subSource;
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
		setParam("source", source);
		setParam("subSource", subSource);
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
//		return "http://testos.mobile.aoshitang.com/v2/v2/appActive.action";
		return "http://testos.mobile.aoshitang.com/v2/v2/appActive.action";
	}
	
	@Override
	public String getReleaseUrl() {
//		return "http://m.zz2mob.aoshitang.com/appActive.action";
		return "http://m.zz2mob.aoshitang.com/appActive.action";
	}
	
	@Override
	public String logTag() {
		return TAG;
	}

}
