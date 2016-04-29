package com.reign.ast.sdk.http;

/**
 * http回调
 * 
 * @author zhouwenjia
 *
 */
public abstract class HttpCallback {

	/**
	 * 成功回调
	 * 
	 * @param code
	 * @param msg
	 * @param data
	 */
	public void onSuccess(int code, String msg, Object data) {

	}

	/**
	 * 失败回调
	 * 
	 * @param code
	 * @param msg
	 * @param data
	 */
	public void onFailure(int code, String msg, Object data) {
	}
}
