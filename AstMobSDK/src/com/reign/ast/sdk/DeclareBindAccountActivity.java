package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.QuickRegHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

public class DeclareBindAccountActivity extends Activity implements
		OnClickListener {

	private String TAG = getClass().getSimpleName();

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);

		UserInfo currUser = AstGamePlatform.getInstance().getUserInfo();

		if (currUser != null) {
			if (getIntent().getStringExtra("from") == null) {
				startActivity(new Intent(this,
						currUser.isQuickUser() ? GuestUserCenterActivity.class
								: AccountUserCenterActivity.class));
				finish();
				return;
			}
		}
		final Dialog dialog = new Dialog(this,
				R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.setContentView(R.layout.ast_mob_sdk_bind_account);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.findViewById(R.id.ignore).setOnClickListener(this);
		dialog.findViewById(R.id.bindnow).setOnClickListener(this);

		new QuickRegHandler(AstGamePlatform.getInstance().isDebugMode(),
				new HttpCallback() {
					public void onSuccess(int code, String msg,
							final Object data) {
						// 登陆处理成功
						Logger.d(TAG, "快速注册 success.");
						UserInfo userInfo = (UserInfo) data;

						GameAccount gameAccount = null;

						if (userInfo.isQuickUser()) {
							gameAccount = new GameAccount(GameAccount.QUICK,
									userInfo.getUserId(), null,
									userInfo.getToken());
							dialog.show();
							return;
						} else {
							gameAccount = new GameAccount(GameAccount.COMMON,
									userInfo.getUserName(), null,
									userInfo.getToken());

						}
						// 用户信息持久化
						GameUtil.saveAccountInfo(getApplicationContext(),
								gameAccount);

						// 设置用户信息
						AstGamePlatform.getInstance().setUserInfo(
								userInfo.setUserName(gameAccount.name));
						// 刷新用户
						UserManager.refreshUser(userInfo);

						// 更新logintype 1: 普通登陆
						GameUtil.loginTypeMark(getApplicationContext(),
								GameAccount.QUICK);

						AstGamePlatform.getInstance().getSwitchUserListener()
								.switchSuccess(0, userInfo);

						finish();

						// startActivity(new Intent(getApplicationContext(),
						// WelcomeActivity.class));
						Toast.makeText(getApplicationContext(), "游客登录成功",
								Toast.LENGTH_LONG).show();
					}

					/**
					 * 快速注册失败处理
					 */
					public void onFailure(int code, final String msg,
							Object data) {
						Logger.e(TAG, "快速注册失败！");
						Toast.makeText(getApplicationContext(),
								"快速注册失败:" + msg, Toast.LENGTH_SHORT);
						Intent intent = new Intent(getApplicationContext(),
								MainLoginActivity.class);
						startActivity(intent);
						finish();
					}
				}, String.valueOf(AstGamePlatform.getInstance().getAppInfo()
						.getGameId())).post();

	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		if (view.getId() == R.id.ignore) {

			new QuickRegHandler(AstGamePlatform.getInstance().isDebugMode(),
					new HttpCallback() {
						public void onSuccess(int code, String msg,
								final Object data) {
							// 登陆处理成功
							Logger.d(TAG, "快速注册 success.");
							UserInfo userInfo = (UserInfo) data;

							GameAccount gameAccount = null;

							if (userInfo.isQuickUser()) {
								gameAccount = new GameAccount(
										GameAccount.QUICK,
										userInfo.getUserId(), null,
										userInfo.getToken());
							} else {
								gameAccount = new GameAccount(
										GameAccount.COMMON,
										userInfo.getUserName(), null,
										userInfo.getToken());
							}
							// 用户信息持久化
							GameUtil.saveAccountInfo(getApplicationContext(),
									gameAccount);

							// 设置用户信息
							AstGamePlatform.getInstance().setUserInfo(
									userInfo.setUserName(gameAccount.name));
							// 刷新用户
							UserManager.refreshUser(userInfo);

							// 更新logintype 1: 普通登陆
							GameUtil.loginTypeMark(getApplicationContext(),
									GameAccount.QUICK);

							AstGamePlatform.getInstance()
									.getSwitchUserListener()
									.switchSuccess(0, userInfo);

							finish();

							Toast.makeText(getApplicationContext(), "游客登录成功",
									Toast.LENGTH_LONG).show();
						}

						/**
						 * 快速注册失败处理
						 */
						public void onFailure(int code, final String msg,
								Object data) {
							Logger.e(TAG, "快速注册失败！");
							Toast.makeText(getApplicationContext(), "快速注册失败:"
									+ msg, Toast.LENGTH_SHORT);
							Intent intent = new Intent(getApplicationContext(),
									MainLoginActivity.class);
							startActivity(intent);
							finish();
						}
					}, String.valueOf(AstGamePlatform.getInstance()
							.getAppInfo().getGameId())).post();
			return;
		} else if (view.getId() == R.id.bindnow) {

			new QuickRegHandler(AstGamePlatform.getInstance().isDebugMode(),
					new HttpCallback() {
						public void onSuccess(int code, String msg,
								final Object data) {
							// 登陆处理成功
							UserInfo userInfo = (UserInfo) data;

							GameAccount gameAccount = null;

							if (userInfo.isQuickUser()) {
								gameAccount = new GameAccount(
										GameAccount.QUICK,
										userInfo.getUserId(), null,
										userInfo.getToken());
							} else {
								gameAccount = new GameAccount(
										GameAccount.COMMON,
										userInfo.getUserName(), null,
										userInfo.getToken());
							}
							// 用户信息持久化
							GameUtil.saveAccountInfo(getApplicationContext(),
									gameAccount);

							// 设置用户信息
							AstGamePlatform.getInstance().setUserInfo(
									userInfo.setUserName(gameAccount.name));

							// 刷新用户
							UserManager.refreshUser(userInfo);

							// 更新logintype 1: 普通登陆
							GameUtil.loginTypeMark(getApplicationContext(),
									GameAccount.QUICK);

							AstGamePlatform.getInstance()
									.getSwitchUserListener()
									.switchSuccess(0, userInfo);
						}

						/**
						 * 快速注册失败处理
						 */
						public void onFailure(int code, final String msg,
								Object data) {
							Toast.makeText(getApplicationContext(), "快速注册失败:"
									+ msg, Toast.LENGTH_SHORT);
						}
					}, String.valueOf(AstGamePlatform.getInstance()
							.getAppInfo().getGameId())).post();

			intent.setClass(this, BindAccountActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
