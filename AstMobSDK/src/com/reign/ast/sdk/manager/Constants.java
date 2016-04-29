package com.reign.ast.sdk.manager;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

/**
 * 常量
 * @author zhouwenjia
 *
 */
public class Constants {
	
	public static final int MAX_MONEY = 100000;
	public static final String server_url = "https://msp.alipay.com/x.htm";
//	public static final String ALIPAY_PLUGIN_NAME = "alipay-msp-3.5.4-pro-1000095-201306191628.apk";
	public static final String ALIPAY_PLUGIN_NAME = "alipay-msp.apk";

	/** 充值数值配置 */
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, String[]> amountMap = new HashMap<Integer, String[]>();
	static {
		int mobileLen = 8;
		String[] mobileMount = new String[mobileLen];
		mobileMount[0] = "10";
		mobileMount[1] = "20";
		mobileMount[2] = "30";
		mobileMount[3] = "50";
		mobileMount[4] = "100";
		mobileMount[5] = "200";
		mobileMount[6] = "300";
		mobileMount[7] = "500";
	
		Constants.amountMap.put(Integer.valueOf(Constants.PayMethod.MOBILE.getValue()), mobileMount);
		
		int telecomLen = 8;
		String[] telecommMount = new String[telecomLen];
		telecommMount[0] = "10";
		telecommMount[1] = "20";
		telecommMount[2] = "30";
		telecommMount[3] = "50";
		telecommMount[4] = "100";
		telecommMount[5] = "200";
		telecommMount[6] = "300";
		telecommMount[7] = "500";
	
		Constants.amountMap.put(Integer.valueOf(Constants.PayMethod.TELECOM.getValue()), telecommMount);
		
		int unicomLen = 8;
		String[] unicomMount = new String[unicomLen];
		unicomMount[0] = "10";
		unicomMount[1] = "20";
		unicomMount[2] = "30";
		unicomMount[3] = "50";
		unicomMount[4] = "100";
		unicomMount[5] = "200";
		unicomMount[6] = "300";
		unicomMount[7] = "500";
	
		Constants.amountMap.put(Integer.valueOf(Constants.PayMethod.UNICOM.getValue()), unicomMount);
	}

	/** 19pay充值卡渠道 */
	@SuppressLint("UseSparseArrays")
	public static Map<String, Integer> cardChannelMap = new HashMap<String, Integer>();
	static {
		cardChannelMap.put("CMJFK", PayMethod.MOBILE.getValue());
		cardChannelMap.put("DXJFK", PayMethod.TELECOM.getValue());
		cardChannelMap.put("LTJFK", PayMethod.UNICOM.getValue());
	}
	
	
	/**
	 * pay method
	 * @author zhouwenjia
	 *
	 */
	public static enum PayMethod {
		MOBILE(1, "chinamobile"), 
		TELECOM(2, "chinatelecom"), 
		UNICOM(3, "chinaunicom"), 
		
		ALI_PAY(4, "alipay"), 
		ALI_WAP(6, "aliwap");

		private int value;
		private String tips;

		public String getTips() {
			return this.tips;
		}

		public void setTips(String tips) {
			this.tips = tips;
		}

		private PayMethod(int value, String tips) {
			this.value = value;
			this.tips = tips;
		}

		public int getValue() {
			return this.value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}
}
