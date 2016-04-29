package com.reign.ast.sdk.http.handler;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.ErrorCodeTransfer;
import com.reign.ast.sdk.pojo.Order;
import com.reign.ast.sdk.util.GameUtil;

/**
 * 充值卡handler
 * @author zhouwenjia
 *
 */
public class PayCardHandler extends BaseHttpHandler {
	
	private static final String TAG = PayCardHandler.class.getSimpleName();
	
	private String cardType;
	private String pm_id;
	private String pc_id;
	private String amount;
	private String cardNum;
	private String cardPassword;
	private String selectAmount;
	private String gold;
	private String gameId;
	private String serverId;
	private String roleId;
	private String userId;
	private String yx;
	private String extra;
	
	/**
	 * 构造函数
	 * @param debugMode
	 * @param callback
	 * @param paramMap
	 */
	public PayCardHandler(boolean debugMode, HttpCallback callback, Map<String, String> paramMap) {
		super(debugMode, callback);
		this.cardType = paramMap.get("cardType");
		this.pm_id = paramMap.get("pm_id");
		this.pc_id = paramMap.get("pc_id");
		this.amount = paramMap.get("amount");
		this.cardNum = paramMap.get("cardNum");
		this.cardPassword = paramMap.get("cardPassword");
		this.selectAmount = paramMap.get("selectAmount");
		this.gold = paramMap.get("gold");
		this.gameId = paramMap.get("gameId");
		this.serverId = paramMap.get("serverId");
		this.roleId = paramMap.get("roleId");
		this.userId = paramMap.get("userId");
		this.yx = paramMap.get("yx");
		this.extra = paramMap.get("extra");
		
	}

	/**
	 * 准备数据
	 */
	@Override
	public void prepareRequestOther() {
		long ts = System.currentTimeMillis();
		setParam("cardType", cardType);
		setParam("pm_id", pm_id);
		setParam("pc_id", pc_id);
		
		setParam("amount", amount);
		setParam("cardNum", cardNum);
		
		setParam("cardPassword", cardPassword);
		setParam("selectAmount", selectAmount);
		setParam("gold", gold);
		setParam("gameId", gameId);
		setParam("serverId", serverId);
		setParam("roleId", roleId);
		setParam("userId", userId);
		setParam("ts", ts + "");
		setParam("yx", yx);
		setParam("extra", extra);
		
		String ticket = GameUtil.get19PaySig(cardType, amount, cardNum, cardPassword, selectAmount, gold, gameId, serverId, roleId, userId, ts, yx, extra);
		setParam("ticket", ticket);
	}

	@Override
	public ResponseEntity parseData(String content) {
		// {"state":1,"code":1,"datas":{"message":"交易创建成功，正在处理中...","orderId":"39926041407237715390"}}
		ResponseEntity entity = new ResponseEntity();
		int code = 1;
		int state = 0;
		String msg = "";
		String retMessage = "";
		String orderId = "";
		try {
			JSONObject jsonObj = new JSONObject(content);
			if (!jsonObj.isNull("state")) {
				state = jsonObj.getInt("state");
			}
			if (!jsonObj.isNull("code")) {
				code = jsonObj.getInt("code");
			}
			if (0 == state) {
				// 失败
				msg = ErrorCodeTransfer.getErrorMsg(code);
			} else {
				// 成功
				if (!jsonObj.isNull("datas")) {
					JSONObject jdatas = jsonObj.getJSONObject("datas");
					if (!jdatas.isNull("message")) {
						retMessage = jdatas.getString("message");
					}
					if (!jdatas.isNull("orderId")) {
						orderId = jdatas.getString("orderId");
					}
					Order order = new Order();
					order.setOrderId(orderId);
					order.setMessage(retMessage);
					entity.setData(order);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			entity.setCode(ErrorCodeTransfer.ERROR_PARSE_DATA);
			entity.setMsg(ErrorCodeTransfer.getErrorMsg(ErrorCodeTransfer.ERROR_PARSE_DATA));
			return entity;
		}
		entity.setCode(code);
		entity.setMsg(msg);
		return entity;
	}

	@Override
	public String getDebugUrl() {
		return "http://testos.mobile.aoshitang.com/v2/v2/jiexunReceive.action";
	}

	@Override
	public String getReleaseUrl() {
		return "http://m.zz2mob.aoshitang.com/jiexunReceive.action";
	}

	@Override
	public String logTag() {
		return TAG;
	}

}
