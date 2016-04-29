package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.LoginBindHandler;
import com.reign.ast.sdk.http.handler.QuickRegHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;

public class LoginBindActivity extends Activity implements OnClickListener {

	Dialog dialog;

	EditText username;

	EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);

		dialog.show();

		dialog.setContentView(R.layout.ast_mob_sdk_login_bind);

		dialog.findViewById(R.id.ast_back_icon).setOnClickListener(
				new android.view.View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						startActivity(new Intent(LoginBindActivity.this,
								BindAccountActivity.class));
						finish();
					}
				});

		dialog.findViewById(R.id.ast_mob_sdk_login_btn)
				.setOnClickListener(this);

		username = (EditText) dialog
				.findViewById(R.id.ast_mob_sdk_login_username_et);
		password = (EditText) dialog
				.findViewById(R.id.ast_mob_sdk_login_passwd_et);
		
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.findViewById(R.id.ast_back_icon).callOnClick();
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		new QuickRegHandler(AstGamePlatform.getInstance().isDebugMode(),
				new HttpCallback() {
					public void onSuccess(int code, String msg,
							final Object data) {
						// 登陆处理成功
						UserInfo userInfo = (UserInfo) data;

						GameAccount gameAccount = null;

						if (userInfo.isQuickUser()) {
							gameAccount = new GameAccount(GameAccount.QUICK,
									userInfo.getUserId(), null,
									userInfo.getToken());
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
								userInfo);

						// 刷新用户
						UserManager.refreshUser(userInfo);

						// 更新logintype 1: 普通登陆
						GameUtil.loginTypeMark(getApplicationContext(),
								GameAccount.QUICK);

						AstGamePlatform.getInstance().getSwitchUserListener()
								.switchSuccess(0, userInfo);
					}

					/**
					 * 快速注册失败处理
					 */
					public void onFailure(int code, final String msg,
							Object data) {
						Toast.makeText(getApplicationContext(),
								"快速注册失败:" + msg, Toast.LENGTH_SHORT);
					}
				}, String.valueOf(AstGamePlatform.getInstance().getAppInfo()
						.getGameId())).post();

	}

	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.ast_mob_sdk_login_btn) {

			if (TextUtils.isEmpty(username.getText())) {
				Toast.makeText(getApplicationContext(), "用户名为空!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(password.getText())) {
				Toast.makeText(getApplicationContext(), "密码为空!",
						Toast.LENGTH_SHORT).show();
				return;
			}

			new LoginBindHandler(
					AstGamePlatform.getInstance().isDebugMode(),
					new HttpCallback() {

						@Override
						public void onSuccess(int code, String msg, Object data) {
							// 登陆处理成功
							UserInfo userInfo = (UserInfo) data;
							final GameAccount gameAccount = new GameAccount(
									userInfo.getMobileBind() == 0 ? GameAccount.COMMON
											: GameAccount.QUICK, username
											.getText().toString(), password
											.getText().toString(), userInfo
											.getToken());
							// 用户信息持久化
							GameUtil.saveAccountInfo(getApplicationContext(),
									gameAccount);

							GameUtil.removeGuest(getApplicationContext());

							// 设置用户信息
							AstGamePlatform.getInstance().setUserInfo(
									userInfo.setUserName(gameAccount.name));
							// 刷新用户
							UserManager.refreshUser(userInfo);

							GameUtil.loginTypeMark(
									getApplicationContext(),
									userInfo.getMobileBind() == 0 ? GameAccount.COMMON
											: GameAccount.QUICK);

							AstGamePlatform.getInstance()
									.getSwitchUserListener()
									.switchSuccess(0, userInfo);
							Toast.makeText(getApplicationContext(), "绑定成功",
									Toast.LENGTH_SHORT).show();

							finish();

							startActivity(new Intent(getApplicationContext(),
									WelcomeActivity.class));
						}

						@Override
						public void onFailure(int code, String msg, Object data) {
							Toast.makeText(getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();
						}

					}, AstGamePlatform.getInstance().getUserInfo()
							.getTmpPlayerId(), username.getText().toString(),
					password.getText().toString()).post();

		}

	}

}
