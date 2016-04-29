package com.reign.ast.sdk.http.handler;

import java.net.URLEncoder;
import java.util.SortedSet;
import java.util.TreeSet;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.AstGamePlatform;

/**
 * 验证码处理
 * 
 * @author zhouwenjia
 * 
 */
public class CaptchaHandler extends BaseHttpHandler {
	private static final String TAG = CaptchaHandler.class.getSimpleName();

	/**
	 * 构造函数
	 * 
	 * @param debugMode
	 * @param callback
	 */
	public CaptchaHandler(boolean debugMode, HttpCallback callback) {
		super(debugMode, callback);
	}

	public void addHeader(String header, String value) {
		this.asyncHttpClient.addHeader("Accept", "image/bmp,image/vnd.wap.wbmp,image/jpg,image/jpeg,image/tiff,image/png,image/gi");
	}

	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		setParam("gameId", AstGamePlatform.getInstance().getAppInfo().gameId);
		SortedSet<String> allParams = new TreeSet<String>();
		try {
			String gid = String.valueOf(AstGamePlatform.getInstance().getAppInfo().gameId);
			allParams.add("gameId=" + URLEncoder.encode(gid, "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理结果返回
	 */
	public ResponseEntity parseData(String content) {
		ResponseEntity entity = new ResponseEntity();
		int code = 0;
		Object data = null;
		String msg = "";
		entity.setCode(code);
		entity.setMsg(msg);
		entity.setData(data);
		return entity;
	}

	public String getDebugUrl() {
		return "";
	}

	public String getReleaseUrl() {
		return "";
	}
	
	@Override
	public void get() {
		// TODO
	}

	public String logTag() {
		return TAG;
	}
}
