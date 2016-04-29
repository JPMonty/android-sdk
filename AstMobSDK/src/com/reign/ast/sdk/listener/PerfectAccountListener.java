package com.reign.ast.sdk.listener;

/**
 * 完善账号监听监听
 * @author zhouwenjia
 *
 */
public abstract interface PerfectAccountListener {
	
	public abstract void success();

	public abstract void failure(int code);
}
