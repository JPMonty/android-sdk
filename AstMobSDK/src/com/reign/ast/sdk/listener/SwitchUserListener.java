package com.reign.ast.sdk.listener;

import com.reign.ast.sdk.pojo.UserInfo;

/**
 * 切换账号监听
 * @author zhouwenjia
 *
 */
public abstract interface SwitchUserListener {
	public static final int SWITCH_SUCCESS = 0;
	public static final int SWITCH_CANCEL = 1;

	public abstract void switchSuccess(int code, UserInfo userInfo);

	public abstract void switchFail(int code, String msg);
}
