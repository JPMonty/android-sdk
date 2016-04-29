package com.reign.ast.sdk.pojo;

/**
 * 充值卡渠道
 * @author zhouwenjia
 *
 */
public class CardChannel {

	/** 支付通道编码 */
	private String pc_id;
	
	/** 支付方式编码 */
	private String pm_id;
	
	/** 支付通道省份 */
	private String province;
	
	/** 支付方式描述 */
	private String desc;
	
	/**
	 * 构造函数
	 * @param pc_id
	 * @param pm_id
	 * @param province
	 * @param desc
	 */
	public CardChannel(String pc_id, String pm_id, String province, String desc) {
		this.pc_id = pc_id;
		this.pm_id = pm_id;
		this.province = province;
		this.desc = desc;
	}

	public String getPc_id() {
		return pc_id;
	}

	public void setPc_id(String pc_id) {
		this.pc_id = pc_id;
	}

	public String getPm_id() {
		return pm_id;
	}

	public void setPm_id(String pm_id) {
		this.pm_id = pm_id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
