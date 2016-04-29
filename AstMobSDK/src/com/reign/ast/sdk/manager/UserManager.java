package com.reign.ast.sdk.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.sax.StartElementListener;
import android.util.Log;
import android.widget.Toast;

import com.reign.ast.sdk.AgainLoginActivity;
import com.reign.ast.sdk.AstLoginActivity;
import com.reign.ast.sdk.AstRegActivity;
import com.reign.ast.sdk.MainLoginActivity;
import com.reign.ast.sdk.WelcomeActivity;
import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.QuickLoginHandler;
import com.reign.ast.sdk.http.handler.TokenLoginHandler;
import com.reign.ast.sdk.listener.LoginCallbackListener;
import com.reign.ast.sdk.listener.PayCallbackListener;
import com.reign.ast.sdk.listener.RefreshUserListener;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

/**
 * UserManager
 * 
 * @author zhouwenjia
 * 
 */
public class UserManager {
	private static final String TAG = UserManager.class.getSimpleName();

	/** 线程handler, 与主线程绑定的handler */
	private Handler mHandler = new Handler(Looper.getMainLooper());

	/** 单例 */
	private static UserManager instance = new UserManager();

	/**
	 * 获得单例
	 * 
	 * @return
	 */
	public static UserManager getInstance() {
		return instance;
	}
	
	private volatile boolean quickLogined = false;

	/**
	 * 登陆
	 * 
	 * @param ctx
	 * @param loginCallbackListener
	 * @param hander
	 */
	public synchronized void login(final Context ctx,
			final LoginCallbackListener loginCallbackListener,
			final Handler hander) {
		if ((null == ctx) || (loginCallbackListener == null)
				|| (hander == null)) {
			// 参数错误
			throw new IllegalArgumentException("params is null!");
		}

//		if (this.quickLogined) {
//			hander.sendEmptyMessage(3);
//			return;
//		}
//		this.quickLogined = true;
		new Thread(new Runnable() {
			public void run() {
				Log.i(UserManager.TAG, "check login.");
				// 获得用户历史
				List<GameAccount> users = GameUtil.getUserHistory(ctx);
				if (0 == users.size()) {
					// 主面板
					// quickLogin(ctx, loginCallbackListener, hander);
					hander.sendEmptyMessage(3);
					Logger.d(UserManager.TAG, "弹出主登录面板.");
					mainLogin(ctx);
					return;
				} else {
					// 有账号
					// 只有一个账号
					if (users.size() == 1) {
						// 之前有账号登录, 取第一个账号
						GameAccount user = (GameAccount) users.get(0);
						if (GameAccount.QUICK == user.type) {
							// 游客登录
							// quickRegister(ctx, loginCallbackListener,
							// hander);
							Logger.d(UserManager.TAG, "quickLogin账号登录.");
							quickLogin(user, ctx, loginCallbackListener, hander);
						} else {
							// 普通登录，或者手机登录
							Logger.d(UserManager.TAG, "tokenLogin账号登录.");
							tokenLogin(user, ctx, loginCallbackListener, hander);
						}
					} else if (users.size() > 1) {
						// 有多个账号
						Logger.d(UserManager.TAG, "chooseToLogin账号登录.");
						chooseToLogin(users, ctx, loginCallbackListener, hander, false);
					}
				}
			}

		}).start();
		
		
	}

	private void mainLogin(Context ctx) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(ctx, MainLoginActivity.class);
		ctx.startActivity(intent);
	}

	/**
	 * 快速登录
	 * 
	 * @param user
	 * @param ctx
	 * @param loginCallbackListener
	 * @param hander
	 */
	private void quickLogin(final GameAccount user, final Context ctx,
			final LoginCallbackListener loginCallbackListener,
			final Handler hander) {
		// TODO
		Logger.d(TAG, "快速登录");
		if (null != hander) {
			hander.sendEmptyMessage(1);
		}
		String gameId = String.valueOf(AstGamePlatform.getInstance()
				.getAppInfo().gameId);
		// 登陆处理
		new QuickLoginHandler(AstGamePlatform.getInstance().isDebugMode(),
				new HttpCallback() {
					public void onSuccess(int code, String msg,
							final Object data) {
						// 登陆处理成功
						Logger.d(UserManager.TAG, "quick login success.");
						GameUtil.saveAccountInfo(ctx, user);
						UserInfo userInfo = (UserInfo) data;
						
						// 设置用户信息
						AstGamePlatform.getInstance().setUserInfo(
								userInfo.setUserName(user.name));
						// 刷新用户
						refreshUser(userInfo);

						// 更新logintype 1: 普通登陆
						GameUtil.loginTypeMark(ctx, GameAccount.QUICK);

						if (null != hander) {
							hander.sendEmptyMessage(3);
						}

						mHandler.post(new Runnable() {
							public void run() {
								if (null != loginCallbackListener) {
									loginCallbackListener.loginSuccess(0,
											(UserInfo) data);
								}
								ctx.startActivity(new Intent(ctx, WelcomeActivity.class));
							}
						});
					}

					/**
					 * 登陆失败处理
					 */
					public void onFailure(int code, final String msg,
							Object data) {
						if (null != hander) {
							hander.sendEmptyMessage(3);
						}
						mHandler.post(new Runnable() {
							public void run() {
								// 显示登陆页面
								login(ctx, user.name, loginCallbackListener);
								Toast.makeText(ctx, msg, Toast.LENGTH_LONG)
										.show();
							}
						});
					}
				}, user.token, gameId).post();
	}

	/**
	 * 多个可登录账号，用户选择账号去登录
	 * 
	 * @param user
	 * @param ctx
	 * @param loginCallbackListener
	 * @param hander
	 */
	public static void chooseToLogin(final List<GameAccount> users,
			final Context ctx,
			final LoginCallbackListener loginCallbackListener,
			final Handler hander, boolean ignoreAutoLogin) {
		// TODO
		if(hander != null) {
			hander.sendEmptyMessage(3);
		}
		Logger.d(UserManager.TAG, "弹出多用户选择登录面板.");
		Intent intent = new Intent();
		
		intent.putParcelableArrayListExtra("users", (ArrayList)users);
		intent.putExtra("ignoreAutoLogin", ignoreAutoLogin);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		intent.setClass(ctx, AgainLoginActivity.class);
		ctx.startActivity(intent);
	}
	
	/**
	 * 普通登陆
	 * 
	 * @param user
	 * @param ctx
	 * @param loginCallbackListener
	 * @param hander
	 */
	private void tokenLogin(final GameAccount user, final Context ctx,
			final LoginCallbackListener loginCallbackListener,
			final Handler hander) {
		Logger.d(TAG, "token登录.");
		if (null != hander) {
			hander.sendEmptyMessage(2);
		}
		String userName = GameUtil.addSuffix(user.name);
		String gameId = String.valueOf(AstGamePlatform.getInstance()
				.getAppInfo().gameId);
		// 登陆处理
		new TokenLoginHandler(AstGamePlatform.getInstance().isDebugMode(),
				new HttpCallback() {
					public void onSuccess(int code, String msg,
							final Object data) {
						// 登陆处理成功
						Logger.d(UserManager.TAG, "token login success.");
						GameUtil.saveAccountInfo(ctx, user);
						UserInfo userInfo = (UserInfo) data;
						// 设置用户信息
						AstGamePlatform.getInstance().setUserInfo(
								userInfo.setUserName(user.name));
						// 刷新用户
						refreshUser(userInfo);

						// 更新logintype 1: 普通登陆
						GameUtil.loginTypeMark(ctx, GameAccount.COMMON);
						
						if (null != hander) {
							hander.sendEmptyMessage(3);
						}

						mHandler.post(new Runnable() {
							public void run() {
								if (null != loginCallbackListener) {
									loginCallbackListener.loginSuccess(0,
											(UserInfo) data);
								}
								Log.i(TAG, "账号登录成功");
								ctx.startActivity(new Intent(ctx, WelcomeActivity.class));
								
							}
						});
					}

					/**
					 * 登陆失败处理
					 */
					public void onFailure(int code, final String msg,
							Object data) {
						if (null != hander) {
							hander.sendEmptyMessage(3);
						}
						mHandler.post(new Runnable() {
							public void run() {
								// 显示登陆页面
								login(ctx, user.name, loginCallbackListener);
								Toast.makeText(ctx, msg, Toast.LENGTH_LONG)
										.show();
							}
						});
					}
				}, user.name, user.token, gameId).post();
	}

	// /**
	// * 快速注册
	// *
	// * @param ctx
	// * @param loginCallbackListener
	// * @param hander
	// */
	// private void quickRegister(final Context ctx,
	// final LoginCallbackListener loginCallbackListener,
	// final Handler hander) {
	// Logger.d(TAG, "quick login.");
	// if (hander != null) {
	// hander.sendEmptyMessage(1);
	// }
	// // 获得gameId
	// String gameId = String.valueOf(AstGamePlatform.getInstance()
	// .getAppInfo().gameId);
	//
	// // 快速注册处理
	// new QuickRegHandler(AstGamePlatform.getInstance().isDebugMode(),
	// new HttpCallback() {
	// public void onSuccess(int code, String msg, Object data) {
	// // 快速注册处理成功
	// final UserInfo user = (UserInfo) data;
	//
	// // 设置userInfo
	// AstGamePlatform.getInstance().setUserInfo(user);
	// // 保存账号信息
	// GameAccount account = null;
	// if (user.isQuickUser()) {
	// // 快速注册用户
	// account = new GameAccount(GameAccount.QUICK,
	// String.valueOf(user.getUserId()), "");
	// } else {
	// // 已绑定用户
	// account = new GameAccount(GameAccount.COMMON,
	// user.getUserName(), "");
	// }
	// GameUtil.saveAccountInfo(ctx, account);
	//
	// // 刷新user
	// refreshUser(user);
	// // 更新登陆type, 0: 快速注册
	// GameUtil.loginTypeMark(ctx, GameAccount.QUICK);
	// if (hander != null) {
	// hander.sendEmptyMessage(3);
	// }
	// mHandler.post(new Runnable() {
	// public void run() {
	// if (null != loginCallbackListener) {
	// loginCallbackListener.loginSuccess(0, user);
	// }
	// Toast.makeText(ctx, "登录账号成功！",
	// Toast.LENGTH_LONG).show();
	// }
	// });
	// Logger.d(UserManager.TAG, "quick login success.");
	// }
	//
	// public void onFailure(int code, final String msg,
	// Object data) {
	// // 处理失败
	// Logger.d(UserManager.TAG, "quick login failure.");
	//
	// if (hander != null) {
	// hander.sendEmptyMessage(3);
	// }
	// mHandler.post(new Runnable() {
	// public void run() {
	// // 登陆失败, 跳转登陆页
	// login(ctx, loginCallbackListener);
	// Toast.makeText(ctx, msg, Toast.LENGTH_LONG)
	// .show();
	// }
	// });
	// }
	// }, gameId).post();
	// }

	/**
	 * 登陆
	 * 
	 * @param ctx
	 * @param loginCallbackListener
	 */
	public void login(Context ctx, LoginCallbackListener loginCallbackListener) {
		AstLoginActivity.login(ctx, loginCallbackListener);
	}

	/**
	 * 登陆
	 * 
	 * @param ctx
	 * @param user
	 * @param loginCallbackListener
	 */
	public void login(Context ctx, String user,
			LoginCallbackListener loginCallbackListener) {
		AstLoginActivity.login(ctx, user, loginCallbackListener);
	}

	/**
	 * login
	 * 
	 * @param ctx
	 * @param user
	 * @param pwd
	 */
	public void login(Context ctx, String user, String pwd) {
		AstLoginActivity.login(ctx, user, pwd);
	}

	/**
	 * 刷新user
	 * 
	 * @param userInfo
	 */
	public static void refreshUser(UserInfo userInfo) {
		RefreshUserListener mRefreshUserListener = AstGamePlatform
				.getInstance().getRefreshUserListener();
		if (mRefreshUserListener != null) {
			mRefreshUserListener.refresh(userInfo);
		}
	}

	// /**
	// * 简单自动登陆
	// *
	// * @param ctx
	// * @param user
	// * @param pwd
	// */
	// public void simpleAutoLogin(final Context ctx, final String user,
	// final String pwd) {
	// final String userName = GameUtil.addSuffix(user);
	//
	// final String password = pwd;
	// String gameId = String.valueOf(AstGamePlatform.getInstance()
	// .getAppInfo().gameId);
	// new AstLoginHandler(AstGamePlatform.getInstance().isDebugMode(),
	// new HttpCallback() {
	// // 登陆处理
	// public void onSuccess(int code, String msg, Object data) {
	// // 登陆成功
	// UserInfo userInfo = (UserInfo) data;
	// // 保存账号信息
	// GameUtil.saveAccountInfo(ctx, new GameAccount(
	// GameAccount.COMMON, userName, password));
	// AstGamePlatform.getInstance().setUserInfo(
	// userInfo.setUserName(user));
	// refreshUser(userInfo);
	//
	// // 登陆类型 1
	// GameUtil.loginTypeMark(ctx, GameAccount.COMMON);
	// }
	//
	// public void onFailure(int code, String msg, Object data) {
	// // 登陆失败
	// Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	// login(ctx, userName, password);
	// }
	// }, userName, password, gameId).post();
	// }

	/**
	 * quit
	 */
	public void quit() {
		// TODO
	}

	/**
	 * 
	 * @param context
	 * @param orderId
	 * @param extraAppData
	 * @param listener
	 */
	public void perfectAccount(Context context, String orderId,
			String extraAppData, PayCallbackListener listener) {
		AstRegActivity.perfectAccount(context, orderId, extraAppData, listener);
	}

}
