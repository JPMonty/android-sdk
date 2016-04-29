package com.reign.ast.sdk.listener;

import com.reign.ast.sdk.pojo.UserInfo;

/**
 * 登陆回调监听
 * @author zhouwenjia
 *
 */
public abstract interface LoginCallbackListener {
	public static final int LOGIN_SUCCESS = 0;
	public static final int LOGIN_CANCEL = 1;

	public abstract void loginSuccess(int code, UserInfo userInfo);

	public abstract void loginFail(int code, String msg);
}
