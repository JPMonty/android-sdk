package com.reign.ast.sdk.http.handler;


import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.Constants;
import com.reign.ast.sdk.pojo.CardChannel;

/**
 * 查询channel
 * @author zhouwenjia
 *
 */
public class QueryChannelHandler extends BaseHttpHandler {
	private static final String TAG = QueryChannelHandler.class.getSimpleName();
	
	private String merchantId;
	private int cardType;
	
	/**
	 * 构造函数
	 * @param debugMode
	 * @param callback
	 * @param merchantId
	 */
	public QueryChannelHandler(boolean debugMode, HttpCallback callback, String merchantId, int cardType) {
		super(debugMode, callback);
		this.merchantId = merchantId;
		this.cardType = cardType;
	}
	/**
	 * 准备数据
	 */
	public void prepareRequestOther() {
		setParam("merchant_id", merchantId);
	}

	/**
	 * 处理结果返回
	 */
	@SuppressLint("UseSparseArrays")
	public ResponseEntity parseData(String content) {
		// CMJFK00010001|CMJFK||全国移动充值卡|DXJFK00010001|DXJFK||中国电信充值付费卡|LTJFK00020000|LTJFK||全国联通一卡充
		String[] arr = content.split("\\|");
		int paramLen = arr.length;
		
		int index = 0;
		Integer tmpCardType = 0;
		String pc_id = null;
		String pm_id = null;
		String province = null;
		String desc = null;
		CardChannel cardChannel = null;
		Map<Integer, CardChannel> map = new HashMap<Integer, CardChannel>();
		while(index < paramLen) {
			pc_id = arr[index++];
			pm_id = arr[index++];
			province = arr[index++];
			desc = arr[index++];
			
			cardChannel = new CardChannel(pc_id, pm_id, province, desc);
			tmpCardType = Constants.cardChannelMap.get(pm_id.toUpperCase());
			if (null == tmpCardType) {
				continue;
			}
			map.put(tmpCardType, cardChannel);
		}
		ResponseEntity entity = new ResponseEntity();
		int code = 1;
		String msg = "";
		entity.setCode(code);
		entity.setMsg(msg);
		entity.setData(map.get(cardType));
		return entity;
	}

	@Override
	public String getDebugUrl() {
//		return "http://219.143.36.225/card/channel.jsp";
		return "http://change.19ego.cn/channel.jsp";
	}

	@Override
	public String getReleaseUrl() {
		return "http://change.19ego.cn/channel.jsp";
	}

	@Override
	public String logTag() {
		return TAG;
	}

}
