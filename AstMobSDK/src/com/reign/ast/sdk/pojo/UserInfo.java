package com.reign.ast.sdk.pojo;

/**
 * userInfo
 * 
 * @author zhouwenjia
 * 
 */
public class UserInfo {
	private String userId;
	private String token;
	private boolean isQuickUser;
	private String userName;
	private String tmpPlayerId;
	private String mobile;
	private Integer mobileBind;

	public Integer getMobileBind() {
		return mobileBind;
	}

	public void setMobileBind(Integer mobileBind) {
		this.mobileBind = mobileBind;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/** 用户来源 */
	private String source;

	public String getUserName() {
		return this.userName;
	}

	public UserInfo setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public boolean isQuickUser() {
		return this.isQuickUser;
	}

	public void setQuickUser(boolean isQuickUser) {
		this.isQuickUser = isQuickUser;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTmpPlayerId() {
		return tmpPlayerId;
	}

	public void setTmpPlayerId(String tmpPlayerId) {
		this.tmpPlayerId = tmpPlayerId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String toString() {
		return "UserInfo [userId=" + this.userId + ", token=" + this.token
				+ ", isQuickUser=" + this.isQuickUser + "]";
	}
}
