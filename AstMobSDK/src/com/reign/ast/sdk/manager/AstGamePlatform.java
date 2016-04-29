package com.reign.ast.sdk.manager;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.reign.ast.sdk.AstLoginActivity;
import com.reign.ast.sdk.AstPayActivity;
import com.reign.ast.sdk.AstProgressDialog;
import com.reign.ast.sdk.AstRegActivity;
import com.reign.ast.sdk.BaseActivity;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.AdActiveHandler;
import com.reign.ast.sdk.http.handler.AndFirstHandler;
import com.reign.ast.sdk.listener.InitCallbackListener;
import com.reign.ast.sdk.listener.LoginCallbackListener;
import com.reign.ast.sdk.listener.PayCallbackListener;
import com.reign.ast.sdk.listener.PerfectAccountListener;
import com.reign.ast.sdk.listener.RefreshUserListener;
import com.reign.ast.sdk.listener.RegistCallbackListener;
import com.reign.ast.sdk.listener.SwitchUserListener;
import com.reign.ast.sdk.pojo.AstAppInfo;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.DeviceUtil;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

/**
 * 游戏平台
 * 
 * @author zhouwenjia
 * 
 */
public class AstGamePlatform {
	private static final String TAG = AstGamePlatform.class.getSimpleName();
	private UserManager userManager = UserManager.getInstance();

	private UserInfo userInfo = null;

	public SwitchUserListener getSwitchUserListener() {
		return switchUserListener;
	}

	private AstAppInfo mAstAppInfo;
	public static Context mCtx;
	private String source;
	public static final int ALIPAY_CHANNEL = 1;
	public static final int UNICOM_CHANNEL = 2;
	public static final int TELCOM_CHANNEL = 3;
	public static final int MOBILE_CHANNEL = 4;
	public static final int ALIWAP_CHANNEL = 5;

	/** 浮动菜单 */
	private FloatMenuManager floatMenuManager;

	/** 刷新user监听 */
	private RefreshUserListener mRefreshUserListener;

	/** 切换user监听 */
	private SwitchUserListener switchUserListener;

	/** 默认为正式环境 */
	private boolean isDebugMode = true;

	/** 单例 */
	private static AstGamePlatform instance = new AstGamePlatform();

	/**
	 * 获得单例
	 * 
	 * @return
	 */
	public static AstGamePlatform getInstance() {
		return instance;
	}

	public String getSource() {
		return this.source;
	}

	/**
	 * 初始化gameplatform
	 * 
	 * @param ctx
	 * @param appInfo
	 * @param listener
	 */
	public void init(Context ctx, AstAppInfo appInfo,
			final InitCallbackListener listener) {
		Log.i(TAG, "@@@ SDK version: " + Environment.VERSION);
		mCtx = ctx;
		this.mAstAppInfo = appInfo;

		this.source = GameUtil.getSource(ctx);
		// 初始化成功回调
		listener.initSuccess();

		// openudid
		DeviceUtil.syncContext(ctx);

		String source = GameUtil.getSource(ctx);
		String subSource = GameUtil.getSubSource(ctx);
		String gameId = String.valueOf(AstGamePlatform.getInstance()
				.getAppInfo().gameId);
		if (GameUtil.hasConfAd(ctx)) {
			// 有配置souce、subSource, 走source、subSource激活
			new AdActiveHandler(AstGamePlatform.getInstance().isDebugMode(),
					new HttpCallback() {
						public void onSuccess(int code, String msg, Object data) {
							Log.d(TAG, "ad-click广告激活成功.");
						}

						public void onFailure(int code, String msg, Object data) {
							Log.d(TAG, "ad-click广告激活失败.");
						}
					}, gameId, source, subSource).post();
		} else {
			// 认为没有配置source、subSource, 走广告激活
			new AndFirstHandler(AstGamePlatform.getInstance().isDebugMode(),
					new HttpCallback() {
						public void onSuccess(int code, String msg, Object data) {
							Log.d(TAG, "andFirst广告激活成功.");
						}

						public void onFailure(int code, String msg, Object data) {
							Log.d(TAG, "andFirst广告激活失败.");
						}
					}, gameId).post();
		}

	}

	public void openLogInfo() {
		Logger.isLog = true;
	}

	public UserInfo getUserInfo() {
		return this.userInfo;
	}

	public boolean isLandscape() {
		if (BaseActivity.orientationType == 1) {
			return false;
		}
		return true;
	}

	public void setOrientation(boolean isPortrait) {
		if (isPortrait) {
			// 竖屏
			BaseActivity.orientationType = 1;
		} else {
			// 横屏
			BaseActivity.orientationType = 2;
		}
	}

	/**
	 * 判断是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		if (null != this.userInfo) {
			return true;
		}
		return false;
	}

	/**
	 * 设置切换user监听
	 * 
	 * @param switchUserListener
	 */
	public void setSwitchUserListener(SwitchUserListener switchUserListener) {
		this.switchUserListener = switchUserListener;
	}

	/**
	 * 注册
	 * 
	 * @param ctx
	 * @param registCallbackListener
	 */
	public void regist(Context ctx,
			RegistCallbackListener registCallbackListener) {
		AstRegActivity.register(ctx, registCallbackListener);
	}

	/**
	 * 登录
	 * 
	 * @param ctx
	 * @param loginCallbackListener
	 */
	public void login(Context ctx, LoginCallbackListener loginCallbackListener) {
		AstProgressDialog.actionLoginActivity(ctx, loginCallbackListener);
	}

	/**
	 * 登出
	 * 
	 * @param ctx
	 * @param loginCallbackListener
	 */
	public void loginout(Context ctx,
			LoginCallbackListener loginCallbackListener) {
		setUserInfo(null);
		login(ctx, loginCallbackListener);
	}

	/**
	 * 登出
	 * 
	 * @param ctx
	 * @return
	 */
	public boolean loginout(Context ctx) {
		setUserInfo(null);
		boolean isLogin = isLogin();
		if (!isLogin) {
			Logger.i(TAG, "loginout ok");
		} else {
			Logger.i(TAG, "loginout error");
		}
		return isLogin;
	}

	/**
	 * pay
	 * 
	 * @param ctx
	 * @param optionData
	 * @param payCallbackListener
	 */
	public void pay(Context ctx, Map<String, Object> optionData,
			PayCallbackListener payCallbackListener) {
		pay(ctx, optionData, payCallbackListener, true);
	}

	/**
	 * pay
	 * 
	 * @param ctx
	 * @param optionData
	 * @param payCallbackListener
	 * @param amountEditable
	 */
	public void pay(Context ctx, Map<String, Object> optionData,
			PayCallbackListener payCallbackListener, boolean amountEditable) {
		if (null == optionData) {
			throw new IllegalArgumentException("pay参数为空");
		}

		if (isLogin()) {
			optionData.put("user", getCurrentUser());
			if (null == optionData.get("amount")) {
				optionData.put("amount", 100f);
			} else {
				Object amount = optionData.get("amount");
				float fAmount = 0f;
				try {
					fAmount = Float.valueOf(amount + "");
				} catch (Exception e) {
					// ignore
				}
				if (fAmount < 0.1) {
					optionData.put("amount", 100f);
				}
			}
			AstPayActivity.pay(ctx, optionData, payCallbackListener,
					amountEditable);
		} else if (null != payCallbackListener) {
			Toast.makeText(ctx, "请先登录", Toast.LENGTH_LONG).show();
			payCallbackListener.payFail(
					10001,
					null,
					null != optionData.get("extraAppData") ? optionData.get(
							"extraAppData").toString() : "");
		}
	}

	public AstAppInfo getAppInfo() {
		return this.mAstAppInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	/**
	 * 调试模式
	 * 
	 * @param mode
	 */
	public void setDebugMode(boolean mode) {
		this.isDebugMode = mode;
		if (mode) {
			// 调试模式, 开log
			openLogInfo();
		}
	}

	public boolean isDebugMode() {
		return this.isDebugMode;
	}

	/**
	 * 获得当前user
	 * 
	 * @return
	 */
	public String getCurrentUser() {
		if (this.userInfo != null) {
			if (!this.userInfo.isQuickUser()) {
				return GameUtil.addSuffix(this.userInfo.getUserName());
			}
			return this.userInfo.getUserId();
		}
		return null;
	}

	/**
	 * 切换账号
	 * 
	 * @param context
	 * @param switchUserListener
	 */
	public void switchUser(Context context,
			SwitchUserListener switchUserListener) {
		if (null == switchUserListener) {
			switchUserListener = this.switchUserListener;
		}
		AstLoginActivity.switchUser(context, switchUserListener);
	}

	public RefreshUserListener getRefreshUserListener() {
		return this.mRefreshUserListener;
	}

	public void addRefreshUserListener(RefreshUserListener refreshUserListener) {
		this.mRefreshUserListener = refreshUserListener;
	}

	/**
	 * 充值记录
	 * 
	 * @param context
	 */
	public void goPayRecord(Context context) {
		if (isLogin()) {
			// TODO
		} else {
			Toast.makeText(context, "please login first", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 反馈
	 * 
	 * @param context
	 */
	public void goFeedBack(Context context) {
		if (isLogin()) {
			// TODO
		} else {
			Toast.makeText(context, "please login first", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 设置isQuickUser
	 * 
	 * @param isQuickUser
	 */
	public void setIsQuickUser(boolean isQuickUser) {
		if (null != this.userInfo) {
			this.userInfo.setQuickUser(isQuickUser);
		}
	}

	/**
	 * 重置menuBar
	 */
	public void resetMenuBar() {
		if (null != floatMenuManager) {
			floatMenuManager.resetMenuBar();
		}
	}

	/**
	 * 完善账号
	 * 
	 * @param ctx
	 * @param listener
	 */
	public void perfectAccount(Context ctx, PerfectAccountListener listener) {
		if (null == listener) {
			listener = new PerfectAccountListener() {
				@Override
				public void success() {
					// 完善账号成功, 重置menuBar
					resetMenuBar();
				}

				@Override
				public void failure(int code) {
				}
			};
		}
		UserInfo userInfo = getUserInfo();
		if (null == userInfo) {
			if (null != listener) {
				listener.failure(10001);
			}
			return;
		}
		if ((userInfo != null) && (userInfo.isQuickUser())) {
			AstRegActivity.perfectAccount(ctx, listener);
		} else {
			Toast.makeText(ctx, "已完善", Toast.LENGTH_LONG).show();
			if (null != listener) {
				listener.failure(10003);
			}
		}
	}

	/**
	 * 创建浮动菜单
	 * 
	 * @param context
	 * @param isFullscreen
	 * @return
	 */
	public FloatMenuManager createFloatMenu(Context context,
			boolean isFullScreen) {
		if (null == floatMenuManager) {
			floatMenuManager = new FloatMenuManager(context, isFullScreen);
		}
		// 先无视isFullScreen参数
		return floatMenuManager;
	}

	/**
	 * onTerminate
	 */
	public void onTerminate() {
		this.userManager.quit();
		mCtx = null;
		this.userInfo = null;
		GameUtil.setSource(null);
		GameUtil.setSubSource(null);
	}

}
