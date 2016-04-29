package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.RegHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

public class RegisterAstActivity extends Activity implements OnClickListener {

	private String TAG = getClass().getSimpleName();

	private CheckBox agree = null;

	private Dialog dialog = null;

	private EditText account = null;

	private EditText password1 = null;

	private EditText password2 = null;

	Handler mhanler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				finish();
				startActivity(new Intent(getApplicationContext(),
						WelcomeActivity.class));
				Toast.makeText(getApplicationContext(), "注册成功！",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
			}
		};

	};

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.show();
		dialog.setContentView(R.layout.ast_mob_sdk_register);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.findViewById(R.id.ast_back_icon).callOnClick();
			}
		});
		// dialog.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(DialogInterface d, int keyCode, KeyEvent event)
		// {
		// if (keyCode == KeyEvent.KEYCODE_BACK) {
		// dialog.findViewById(R.id.ast_back_icon).callOnClick();
		// return false;
		// } else {
		// return true;
		// }
		// }
		// });
		account = (EditText) dialog
				.findViewById(R.id.ast_mob_sdk_register_username_et);
		password1 = (EditText) dialog
				.findViewById(R.id.ast_mob_sdk_register_passwd_et);
		password2 = (EditText) dialog
				.findViewById(R.id.ast_mob_sdk_register_passwd_et_again);
		agree = (CheckBox) dialog.findViewById(R.id.agree);
		agree.setClickable(true);
		dialog.findViewById(R.id.ast_back_icon).setOnClickListener(this);
		dialog.findViewById(R.id.ast_mob_sdk_next_step_btn).setOnClickListener(
				this);
		dialog.findViewById(R.id.sjhzc).setOnClickListener(this);
		dialog.findViewById(R.id.ysxy).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		if (view.getId() == R.id.ast_back_icon) {
			intent.setClass(this, AstLoginActivity.class);
		} else if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {
			if (!agree.isChecked()) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = "请先同意隐私协议";
				mhanler.sendMessage(msg);
				return;
			} else if (!account.getText().toString().matches("\\w{5,20}")) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = "用户名不符合规则";
				mhanler.sendMessage(msg);

			} else if (!password1.getText().toString().matches(".{6,20}")) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = "密码不符合规则";
				mhanler.sendMessage(msg);
			} else if (!password1.getText().toString()
					.equals(password2.getText().toString())) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = "两次密码输入不一致";
				mhanler.sendMessage(msg);
			} else {

				new RegHandler(AstGamePlatform.getInstance().isDebugMode(),
						new HttpCallback() {
							public void onSuccess(int code, String msg,
									final Object data) {
								// 登陆处理成功
								Logger.d(TAG, "register success.");
								UserInfo userInfo = (UserInfo) data;

								final GameAccount gameAccount = new GameAccount(
										GameAccount.COMMON, userInfo.getUserName(), password1
												.getText().toString(), userInfo
												.getToken());
								// 用户信息持久化
								GameUtil.saveAccountInfo(
										getApplicationContext(), gameAccount);

								// 设置用户信息
								AstGamePlatform.getInstance().setUserInfo(
										userInfo.setUserName(gameAccount.name));
								// 刷新用户
								UserManager.refreshUser(userInfo);

								// 更新logintype 1: 普通登陆
								GameUtil.loginTypeMark(getApplicationContext(),
										GameAccount.COMMON);

								AstGamePlatform.getInstance()
										.getSwitchUserListener()
										.switchSuccess(0, userInfo);

								Message msgg = new Message();

								msgg.what = 1;

								msgg.obj = "注册成功";

								mhanler.sendMessage(msgg);

								finish();

								startActivity(new Intent(
										getApplicationContext(),
										WelcomeActivity.class));
							}

							/**
							 * 登陆失败处理
							 */
							public void onFailure(int code, final String msg,
									Object data) {
								Message msgg = new Message();
								msgg.what = 0;
								msgg.obj = msg;
								mhanler.sendMessage(msgg);

							}
						}, account.getText().toString(), password1.getText()
								.toString(), String.valueOf(AstGamePlatform
								.getInstance().getAppInfo().getGameId()))
						.post();

			}
			return;
		} else if (view.getId() == R.id.sjhzc) {
			intent.setClass(this, PhoneLoginActivity.class);
		} else if (view.getId() == R.id.ysxy) {
			System.out.println("打开隐私协议");
			intent.setClass(this, PrivacyActivity.class);
			startActivity(intent);
			return;
		}
		finish();
		startActivity(intent);
	}

	public void onAgree(View view) {
		agree.setChecked(!agree.isChecked());
		agree.setBackgroundResource(agree.isChecked() ? R.drawable.select_on
				: R.drawable.select_bg);
	}

	// @Override
	// public void onBackPressed() {
	// // super.onBackPressed();
	// // System.out.println("按下了back键   onBackPressed()");
	// super.onBackPressed();
	// dialog.findViewById(R.id.ast_back_icon).callOnClick();
	// }

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	// System.out.println("按下了back键   onKeyDown()");
	// return true;
	// } else {
	// return super.onKeyDown(keyCode, event);
	// }

	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// dialog.findViewById(R.id.ast_back_icon).callOnClick();
	// return true;
	// } else {
	// return super.onKeyDown(keyCode, event);
	// }

	// }

}
