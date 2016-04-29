package com.reign.ast.sdk;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.QuickLoginHandler;
import com.reign.ast.sdk.http.handler.TokenLoginHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

public class AgainLoginActivity extends DropDownEditTextActivity implements
		OnClickListener {
	List<GameAccount> users = null;

	CheckBox rememberme = null;

	String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		GameAccount account = null;
		if (!getIntent().getBooleanExtra("ignoreAutoLogin", false)) {
			account = loadRememberAccount();
		}
		if (account != null) {
			login(account);
		}
		Dialog dialog = new Dialog(this,
				R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.setContentView(R.layout.ast_mob_sdk_token_login);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				finish();
			}
		});
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		dialog.show();
		dialog.findViewById(R.id.other_account).setOnClickListener(this);
		dialog.findViewById(R.id.ast_mob_sdk_next_step_btn).setOnClickListener(
				this);
		parent = (RelativeLayout) dialog.findViewById(R.id.parent);
		et = (EditText) dialog.findViewById(R.id.ast_mob_sdk_login_username_et);
		image = (ImageView) dialog.findViewById(R.id.btn_select);
		rememberme = (CheckBox) dialog.findViewById(R.id.rememberme);
		rememberme.setChecked(true);

		final List<String> mList = new ArrayList<String>();

		users = getIntent().getParcelableArrayListExtra("users");

		for (GameAccount user : users) {
			if (user.getType() == GameAccount.QUICK) {
				mList.add("游客");
			} else {
				mList.add(user.name);
			}
		}
		if (mList.size() > 0) {
			et.setText(mList.get(0));
		}
		initDatas(mList);

		initWedget(dialog);

	}

	/**
	 * 初始化填充Adapter所用List数据
	 */
	private void initDatas(List<String> mList) {
		datas.clear();
		datas.addAll(mList);
	}

	private GameAccount loadRememberAccount() {
		SharedPreferences pref = getSharedPreferences(
				"login_remember_account_info", Context.MODE_PRIVATE);
		String content = pref.getString("login_account_info", null);
		GameAccount account = null;
		if (content != null && !"".equals(content)) {
			account = new GameAccount();
			String[] ats = content.split(":");
			if (ats.length == 4) {
				account.type = Integer.parseInt(ats[0]);
				account.name = ats[1];
				account.pwd = ats[2];
				account.token = ats[3];
			}

		}
		return account;
	}

	private void saveRememberAccount(GameAccount account) {
		SharedPreferences pref = getSharedPreferences(
				"login_remember_account_info", 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("login_account_info", account.type + ":"
				+ account.name + ":" + account.pwd + ":" + account.token);
		boolean is = editor.commit();
		Log.i(TAG, "saveRememberAccount:" + is);
	}

	private void login(final GameAccount user) {

		if (user.type == GameAccount.QUICK) {
			// 登陆处理
			new QuickLoginHandler(AstGamePlatform.getInstance().isDebugMode(),
					new HttpCallback() {
						public void onSuccess(int code, String msg,
								final Object data) {
							// 登陆处理成功
							Logger.d(TAG, "quick login success.");
							GameUtil.saveAccountInfo(getApplicationContext(),
									user);
							UserInfo userInfo = (UserInfo) data;
							// 设置用户信息
							AstGamePlatform.getInstance().setUserInfo(
									userInfo.setUserName(user.name));
							// 刷新用户
							UserManager.refreshUser(userInfo);
							// 更新logintype 1: 快速登陆
							GameUtil.loginTypeMark(getApplicationContext(),
									GameAccount.QUICK);
							AstGamePlatform.getInstance()
									.getSwitchUserListener()
									.switchSuccess(0, userInfo);
							//Toast.makeText(getApplicationContext(), "游客, 登录成功",
								//	Toast.LENGTH_SHORT).show();
							startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
							finish();
						}

						/**
						 * 登陆失败处理
						 */
						public void onFailure(int code, final String msg,
								Object data) {
							Toast.makeText(getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();

						}
					}, user.token, String.valueOf(AstGamePlatform.getInstance()
							.getAppInfo().getGameId())).post();

		} else if (user.type == GameAccount.COMMON
				|| user.type == GameAccount.PHONE) {
			// 登陆处理
			new TokenLoginHandler(AstGamePlatform.getInstance().isDebugMode(),
					new HttpCallback() {
						public void onSuccess(int code, String msg, Object data) {
							// 成功回调
							super.onSuccess(code, msg, data);
							user.name = GameUtil.addSuffix(user.name);

							UserInfo newUser = (UserInfo) data;

							GameUtil.saveAccountInfo(getApplicationContext(),
									new GameAccount(user.type, user.name,
											user.pwd, newUser.getToken()));

							AstGamePlatform.getInstance().setUserInfo(
									newUser.setUserName(user.name));

							UserManager.refreshUser(newUser);

							GameUtil.loginTypeMark(getApplicationContext(),
									user.type);

							AstGamePlatform.getInstance()
									.getSwitchUserListener()
									.switchSuccess(0, newUser);
							startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
							finish();
						}

						public void onFailure(int code, String msg, Object data) {
							// 失败回调
							Toast.makeText(getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();
							AstLoginActivity.login(getApplicationContext(),
									user.name);
							finish();
						}
					}, user.name, user.token, String.valueOf(AstGamePlatform
							.getInstance().getAppInfo().gameId)).post();

		}

	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		if (view.getId() == R.id.other_account) {
			intent.setClass(this, AstLoginActivity.class);
		} else if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {
			if (TextUtils.isEmpty(et.getText())) {
				startActivity(new Intent(this, MainLoginActivity.class));
				return;
			}

			final GameAccount user = getUserByName(et.getText().toString());
			// 登陆处理
			login(user);
			if (rememberme.isChecked()) {
				saveRememberAccount(user);
			} else {
				removeRememberAccount();
			}
			return;
		}
		finish();
		startActivity(intent);
	}

	private GameAccount getUserByName(String name) {
		for (GameAccount user : users) {
			if (name.equals("游客")) {
				if (user.getType() == GameAccount.QUICK) {
					return user;
				}
			} else if (user.name.equals(name)) {
				return user;
			}
		}
		return null;
	}

}
