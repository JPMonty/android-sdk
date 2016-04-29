package com.reign.ast.sdk.listener;

/**
 * 注册回调监听
 * @author zhouwenjia
 *
 */
public abstract interface RegistCallbackListener {
	
	public abstract void regSucess(int code, String msg, String userName, String passpword);

	public abstract void regFail(int code, String msg);
}
