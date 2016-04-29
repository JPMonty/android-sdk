package com.reign.sdk;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.reign.ast.sdk.WelcomeActivity;
import com.reign.ast.sdk.listener.InitCallbackListener;
import com.reign.ast.sdk.listener.LoginCallbackListener;
import com.reign.ast.sdk.listener.PayCallbackListener;
import com.reign.ast.sdk.listener.SwitchUserListener;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.pojo.AstAppInfo;
import com.reign.ast.sdk.pojo.UserInfo;

public class Ast{

	private static final String TAG = "channel-ast";

	static final int gameId = 112;
	final String gameName = "激战僵尸";

	final String regKey = "!~zz2app:*:register:*:key~@";
	final String loginKey = "!~zz2app:*:login:*:key~@";
	final String bindKey = "!~zz2app:*:bind:*:key~@";
	final String payKey = "!~zz2app:*:order:*:key~@";
	final String activeKey = "!~zz2app:*:active:*:key~@";
	final String changePwdKey = "!~zz2app:*:changepwd:*:key~@";
	final String merchantId = "220267";
	public static AstAppInfo astAppInfo = null;
	
	
	private Context context;
	private Activity activity;

	/** 是否已初始化 */
	private boolean isInit = false;

	/** 浮动菜单 */
//	private FloatMenuManager floatMenu;
	
	private AstGamePlatform astGamePlatform = AstGamePlatform.getInstance();
	
	private final Object lock = new Object();

	/**
	 * 初始�?
	 */
	public void initSDK(Activity a, Context c) {
		context = c;
		activity = a;

		astAppInfo = new AstAppInfo();

		astAppInfo.gameId = gameId;
		astAppInfo.gameName = gameName;
		astAppInfo.regKey = regKey;
		astAppInfo.loginKey = loginKey;
		astAppInfo.bindKey = bindKey;
		astAppInfo.payKey = payKey;
		astAppInfo.activeKey = activeKey;
		// astAppInfo.channel = ChannelManager.getInstance().getChannelId();
		astAppInfo.channel = "123456";
		astAppInfo.merchantId = merchantId;
		astAppInfo.changePwdKey = changePwdKey;
		// debug模式
		astGamePlatform.setDebugMode(true);

		// 设置切换账号回调
		astGamePlatform.setSwitchUserListener(new SwitchUserListener() {
			@Override
			public void switchSuccess(int code, UserInfo userInfo) {
				// 切换账号成功
				clickLogout(code, userInfo);
			}

			@Override
			public void switchFail(int code, String msg) {
			}
		});

		
		// 初始化sdk
		astGamePlatform.init(activity, astAppInfo, new InitCallbackListener() {
			@Override
			public void initSuccess() {
				// 初始化sdk成功
				Log.e(TAG, "@@@init sdk success.");
			}

			@Override
			public void initFail(int code, String msg) {
				// 初始化sdk失败
				Log.e(TAG, "@@@init sdk fail, code: " + code + ", msg: " + msg);
			}
		});
		this.isInit = true;
	}

	
	
	
	
	
	/**
	 * 是否已初始化
	 * 
	 * @return
	 */
	public boolean isInit() {
		return this.isInit;
	}

	/**
	 * 登陆
	 */
	public void userLogin() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 登陆
				astGamePlatform.login(activity, new LoginCallbackListener() {
					@Override
					public void loginSuccess(int code, UserInfo userInfo) {
						// 登录成功
						Log.e(TAG,
								"@@@login success user: "
										+ userInfo.getUserName());
						String userId = String.valueOf(userInfo.getUserId());
						String token = userInfo.getToken();
						String yxSource = astGamePlatform.getSource();
						// 登陆传参给lua
						clickLogin(userId, token, yxSource);
						
//						synchronized (lock) {
//							if (null == floatMenu) {
//								floatMenu = astGamePlatform.createFloatMenu(
//										activity, true);
//								floatMenu.setParamsXY(-1, -1);
//							} else {
//								floatMenu.setParamsXY(-1, -1);
//							}
//							if (null != floatMenu) {
//								floatMenu.show();
//							}
//						}
						
						
//						activity.runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								
//							}
//						});
						// ChannelManager.getInstance().setFloatMenuVisible(false);
					}

					@Override
					public void loginFail(int code, String msg) {
						// 登录失败
						Log.e(TAG, "@@@login fail, code: " + code + ", msg: "
								+ msg);
					}
				});
			}
		}).start();
	}

	/**
	 * 切换账号
	 */
	public void userLogout() {
		this.activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				astGamePlatform.switchUser(activity, new SwitchUserListener() {

					@Override
					public void switchSuccess(int code, UserInfo userInfo) {
						Log.d(TAG, "@@@switch user success, code: " + code
								+ ", userInfo: " + userInfo.getUserName());
						// 切换账号成功
						clickLogout(code, userInfo);

						// String userId = String.valueOf(userInfo.getUserId());
						// String token = userInfo.getToken();
						// String yxSource = astGamePlatform.getSource();
						// // 登陆传参给lua
						// clickLogin(userId, token, yxSource);

					}

					@Override
					public void switchFail(int code, String msg) {
						Log.e(TAG, "@@@switch fail code: " + code + ", msg: "
								+ msg);
					}
				});
			}
		});
	}

	public void showAccountCenter() {
		userLogout();
	}

	/**
	 * 隐藏悬浮�? * @param visible
	 */
	public void setFloatMenuVisible(boolean visible) {
		this.activity.runOnUiThread(new CustomThread(visible));
		// this.activity.runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// if (null != floatMenu) {
		// if (true) {
		// floatMenu.hide();
		// } else {
		// floatMenu.show();
		// }
		// }
		// }
		// });
	}

	public void guestLogin() {
	}

	/**
	 * �?��sdk
	 */
	public void destorySdk() {
		this.activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				floatMenu.hide();
				astGamePlatform.onTerminate();
			}
		});
	}

	/**
	 * pay
	 */
	public void pay(String userId, String playerId, String playerName,
			String serverId, String args) {
		Log.e(TAG, "userId: " + userId);
		Log.e(TAG, "playerId: " + playerId);
		Log.e(TAG, "playerName: " + playerName);
		Log.e(TAG, "serverId: " + serverId);
		Log.e(TAG, "args: " + args);

		JSONObject jsonArgs;
		try {
			jsonArgs = new JSONObject(args);
			int money = jsonArgs.getInt("money");
			String orderId = jsonArgs.getString("orderId");
			int gold = jsonArgs.getInt("gold");
			String itemId = jsonArgs.getString("itemId");
			String yx = jsonArgs.getString("yx");
			String extraData = String.format("%s-%s-%s-%s-%s", serverId,
					playerId, userId, orderId, itemId);
			String des = String.format("%d钻石", gold);

			Map<String, Object> data = new HashMap<String, Object>();
			// 游戏中货币名�? data.put("currency", "钻石");

			// 人民币兑换比�? data.put("rate", 10);

			// 默认金额
			data.put("amount", money / 100.0f);

			// 购买商品名字
			data.put("product_name", des);

			// role id
			data.put("roleId", playerId);

			// server id
			data.put("serverId", serverId);

			// yx
			data.put("yx", yx);

			// 额外数据
			data.put("extraAppData", extraData);

			data.put("extra", extraData);

			astGamePlatform.pay(activity, data, new PayCallbackListener() {
				// 支付成功回调,游戏方可以做后续逻辑处理
				// 收到该回调说明提交订单成功，但成功与否要以服务器回调通知为准
				@Override
				public void paySuccess(String orderId, String extraAppData) {
					Log.e(TAG, "paySuccess orderId: " + orderId
							+ ", extraAppData:" + extraAppData);
				}

				@Override
				public void payFail(int code, String orderId,
						String extraAppData) {
					// 支付失败情况�?orderId可能为空
					if (orderId != null) {
						Log.e(TAG, "payFail code: " + code + ", orderId: "
								+ orderId + ", extraAppData: " + extraAppData);
					} else {
						Log.e(TAG, "payFail code: " + code + ", extraAppData: "
								+ extraAppData);
					}
				}
			}, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * activity resume容错处理
	 */
	public void onResumeMakeup() {
//		if (null == floatMenu) {
//			floatMenu = astGamePlatform.createFloatMenu(activity, true);
//			floatMenu.setParamsXY(-1, -1);
//			floatMenu.show();
//		}
	}

	/**
	 * 登陆-传参给lua
	 * 
	 * @param userId
	 * @param token
	 */
	public void clickLogin(String userId, String token, String yxSource) {
		System.out.println("登录-userId:" + userId + ", token:" + token
				+ "yxSource:" + yxSource);
		// ChannelManager.nativeMessageBegin();
		// ChannelManager.nativeAddInt("state", 1);
		// ChannelManager.nativeAddString("channelFlag",
		// ChannelManager.getInstance().getChannelId());
		// ChannelManager.nativeAddString("action", "login");
		// ChannelManager.nativeAddString("userId", userId);
		// ChannelManager.nativeAddString("token", token);
		// ChannelManager.nativeAddString("yxSource", yxSource);
		// ChannelManager.nativeMessageEnd();
	}

	/**
	 * 登出
	 * 
	 * @param code
	 * @param userInfo
	 */
	public void clickLogout(int code, UserInfo userInfo) {
		System.out.println("登出-code:" + code + ", 新userInfo:" + userInfo);
		// ChannelManager.nativeMessageBegin();
		// ChannelManager.nativeAddInt("state", 1);
		// ChannelManager.nativeAddString("channelFlag",
		// ChannelManager.getInstance().getChannelId());
		// ChannelManager.nativeAddString("action", "logout");
		// ChannelManager.nativeAddString("code", code + "");
		// ChannelManager.nativeAddString("userId", (null != userInfo ?
		// userInfo.getUserId() : ""));
		// ChannelManager.nativeMessageEnd();
	}

	/**
	 * 获得context
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	class CustomThread implements Runnable {

		private boolean visible;

		public CustomThread(boolean visible) {
			this.visible = visible;
		}

		@Override
		public void run() {
//			if (null != floatMenu) {
//				if (!visible) {
//					floatMenu.hide();
//				} else {
//					floatMenu.show();
//				}
//			}
		}

	}
}
