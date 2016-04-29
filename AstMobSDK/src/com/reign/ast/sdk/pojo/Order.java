package com.reign.ast.sdk.pojo;

import java.io.Serializable;

/**
 * 订单
 * @author zhouwenjia
 * 
 */
public class Order implements Serializable {
	private static final long serialVersionUID = 157921608225126267L;
	private String orderId;
	private String amount;
	private String message;
	private String server;
	private Integer status;
	private String sign;
	private String wapUrl;
	private String payDate;

	public String getAmount() {
		return this.amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getServer() {
		return this.server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPayDate() {
		return this.payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public String getWapUrl() {
		return this.wapUrl;
	}

	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return "Order [orderId=" + this.orderId + ", amount=" + this.amount
				+ ", server=" + this.server + ", status=" + this.status
				+ ", sign=" + this.sign + ", wapUrl=" + this.wapUrl + ", payDate="
				+ this.payDate + "]";
	}
}
