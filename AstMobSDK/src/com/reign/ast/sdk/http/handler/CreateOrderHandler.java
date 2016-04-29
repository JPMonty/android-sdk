package com.reign.ast.sdk.http.handler;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.reign.ast.sdk.http.BaseHttpHandler;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.ResponseEntity;
import com.reign.ast.sdk.manager.ErrorCodeTransfer;
import com.reign.ast.sdk.pojo.Order;
import com.reign.ast.sdk.util.GameUtil;

/**
 * 创建订单
 * @author zhouwenjia
 *
 */
public class CreateOrderHandler extends BaseHttpHandler {
	
	private static final String TAG = CreateOrderHandler.class.getSimpleName();
	
	private String userId;
	private String gameId;
	private String gameName;
	private String extraAppData;
	private String serverId;
	private String roleId;
	private String money;
	private String gold;
	private String yx;
	private String extra;
	

	/**
	 * 构造函数
	 * @param debugMode
	 * @param callback
	 * @param paramMap
	 */
	public CreateOrderHandler(boolean debugMode, HttpCallback callback, Map<String, String> paramMap) {
		super(debugMode, callback);
		this.userId = paramMap.get("userId");
		this.gameId = paramMap.get("gameId");
		
		this.gameName = paramMap.get("gameName");
		this.extraAppData = paramMap.get("extraAppData");
		
		this.serverId = paramMap.get("serverId");
		this.roleId = paramMap.get("roleId");
		this.money = paramMap.get("money");
		this.gold = paramMap.get("gold");
		this.yx = paramMap.get("yx");
		this.extra = paramMap.get("extra");
	}

	/**
	 * 准备数据
	 */
	@Override
	public void prepareRequestOther() {
		
		long ts = System.currentTimeMillis();
		setParam("userId", userId);
		setParam("gameId", gameId);
		setParam("gameName", gameName);
		setParam("extraAppData", extraAppData);
		setParam("serverId", serverId);
		setParam("roleId", roleId);
		setParam("money", money);
		setParam("gold", gold);
		setParam("ts", ts + "");
		setParam("yx", yx);
		setParam("extra", extra);
		String ticket = GameUtil.getCreateOrderSig(userId, gameId, gameName, extraAppData, serverId, roleId, money, gold, ts, yx, extra);
		setParam("ticket", ticket);
	}

	/**
	 * 处理返回
	 */
	@Override
	public ResponseEntity parseData(String content) {
		// {"state":1,"code":1,"datas":{"sign":"partner=\"2088501495396266\"&seller_id=\"381607798@qq.com\"&out_trade_no=\"87017831407491587947\"&subject=\"《征战四方》充值\"&body=\"购买金币-2-2-110010-14074915742226-2\"&total_fee=\"10.0\"&notify_url=\"http://testos.mobile.aoshitang.com/v2/v2/alipayReceive.action\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&sign=\"F974XoLSBXjJQI0YmY%2FuqZcs2fE4ei92YZin4z1MWPEeo5OLkOelLMprH1uAhTTyygijOrQuJkOLOH8GzE6MNpjv1AmU53xW1BUTUsnYYonpWDFACZgK4aY16pibkNBtDv0t65sdmMW%2Fvg%2BsaTYLJ6jWu%2Bi5JM26RF6BxEVHBS4%3D\"&sign_type=\"RSA\"","token":"5faae7cde491dd832266981f5268b0a8","orderId":"87017831407491587947","timestrap":1407491587952}}
		ResponseEntity entity = new ResponseEntity();
		int state = 0, code = 0;
		String msg = "";
		String sign = "";
		String token = null, orderId = null;
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
					if (!jdatas.isNull("token")) {
						token = jdatas.getString("token");
					}
					if (!jdatas.isNull("orderId")) {
						orderId = jdatas.getString("orderId");
					}
					if (!jdatas.isNull("sign")) {
						sign = jdatas.getString("sign");
					}
					Log.d(logTag(), "token: " + token + ", orderId: " + orderId + ", sign: " + sign);
					Order order = new Order();
					order.setOrderId(orderId);
					order.setAmount(money);
					order.setSign(sign);
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
		return "http://testos.mobile.aoshitang.com/v2/v2/alipayCreateOrder.action";
	}

	@Override
	public String getReleaseUrl() {
		return "http://m.zz2mob.aoshitang.com/alipayCreateOrder.action";
	}

	@Override
	public String logTag() {
		return TAG;
	}

}
