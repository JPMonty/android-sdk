package com.reign.ast.sdk.listener;

import com.reign.ast.sdk.pojo.UserInfo;

/**
 * 刷新user监听
 * @author zhouwenjia
 *
 */
public abstract interface RefreshUserListener {
	
	public abstract void refresh(UserInfo userInfo);
}
