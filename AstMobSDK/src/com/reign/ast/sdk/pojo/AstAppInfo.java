package com.reign.ast.sdk.pojo;

import android.content.Context;

/**
 * appInfo
 * 
 * @author zhouwenjia
 * 
 */
public class AstAppInfo {

	public Context context;

	public int gameId;
	public String gameName;

	public String regKey;
	public String loginKey;
	public String bindKey;
	public String payKey;
	public String activeKey;
	public String changePwdKey;

	public String getChangePwdKey() {
		return changePwdKey;
	}

	public void setChangePwdKey(String changePwdKey) {
		this.changePwdKey = changePwdKey;
	}

	/** 渠道 */
	public String channel;

	/** 19pay商户id */
	public String merchantId;

	public Context getContext() {
		return this.context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getGameName() {
		return this.gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getRegKey() {
		return regKey;
	}

	public void setRegKey(String regKey) {
		this.regKey = regKey;
	}

	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	public String getBindKey() {
		return bindKey;
	}

	public void setBindKey(String bindKey) {
		this.bindKey = bindKey;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getPayKey() {
		return payKey;
	}

	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}

	public String getActiveKey() {
		return activeKey;
	}

	public void setActiveKey(String activeKey) {
		this.activeKey = activeKey;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

}
