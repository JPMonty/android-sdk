package com.reign.ast.sdk.listener;

/**
 * 初始化回调监听
 * @author zhouwenjia
 *
 */
public abstract interface InitCallbackListener {
	public static final int INIT_SUCCESS = 0;
	public static final int INIT_ERROR = 1;

	public abstract void initSuccess();

	public abstract void initFail(int code, String msg);
}
