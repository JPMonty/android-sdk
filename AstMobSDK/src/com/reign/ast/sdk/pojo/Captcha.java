package com.reign.ast.sdk.pojo;


/**
 * 验证码
 * TODO
 * @author zhouwenjia
 * 
 */
public class Captcha {
	String url;
	String captchaId;

	public String getCaptchaId() {
		return this.captchaId;
	}

	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
