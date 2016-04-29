package com.reign.ast.sdk.listener;

/**
 * 充值回调监听
 * @author zhouwenjia
 *
 */
public abstract interface PayCallbackListener {
	
	public abstract void paySuccess(String orderId, String extraAppData);

	public abstract void payFail(int code, String orderId, String extraAppData);
}
