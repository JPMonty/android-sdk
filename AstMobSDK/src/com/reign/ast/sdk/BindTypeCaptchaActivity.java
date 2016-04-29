package com.reign.ast.sdk;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.AstBindHandler;
import com.reign.ast.sdk.http.handler.SendBindAccountCaptchaHandler;
import com.reign.ast.sdk.http.handler.ValidateBindCaptchaHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.pojo.UserInfo;
import com.ta.utdid2.android.utils.StringUtils;

public class BindTypeCaptchaActivity extends Activity implements
		OnClickListener {

	Dialog dialog = null;

	Button sendCaptchaAgain = null;
	Boolean canSendAgain = false;
	Integer count = 60;
	String mobile = null;
	EditText codeText = null;

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.setContentView(R.layout.ast_mob_sdk_phone_bind_captcha);
		dialog.show();
		dialog.findViewById(R.id.ast_back_icon).setOnClickListener(this);
		dialog.findViewById(R.id.ast_mob_sdk_next_step_btn).setOnClickListener(
				this);

		dialog.findViewById(R.id.ysxy).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						System.out.println("打开隐私协议");
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(),
								PrivacyActivity.class);
						startActivity(intent);
						return;
					}
				});
		codeText = (EditText) dialog.findViewById(R.id.ast_mob_sdk_phone_et);

		sendCaptchaAgain = (Button) dialog.findViewById(R.id.sendAgain);
		String mobile = getIntent().getStringExtra("mobile");
		this.mobile = mobile;
		sendCaptchaAgain.setClickable(false);
		if (mobile != null) {
			TextView phone_number = (TextView) dialog
					.findViewById(R.id.phone_number);
			phone_number.setText(mobile);
		} else {
			finish();
		}

		sendCaptchaAgain.setText(count.toString());
		sendCaptchaAgain.setOnClickListener(this);
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (count > 1) {
							sendCaptchaAgain.setText((--count).toString());
						} else {
							sendCaptchaAgain.setClickable(true);
							canSendAgain = true;
							count = 60;
							dialog.findViewById(R.id.ast_mob_sdk_next_step_btn)
									.setClickable(true);
							sendCaptchaAgain.setText("重试");
							timer.cancel();
						}
					}
				});

			}
		}, 0, 1000);
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
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		if (view.getId() == R.id.ast_back_icon) {
			intent.setClass(this, BindAccountActivity.class);
		} else if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {
			if (!((CheckBox) dialog.findViewById(R.id.agree)).isChecked()) {
				Toast.makeText(getApplicationContext(), "请同意隐私协议",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (!StringUtils.isEmpty(codeText.getText().toString())) {

				final UserInfo currUser = AstGamePlatform.getInstance()
						.getUserInfo();

				if (currUser.isQuickUser()) {
					new ValidateBindCaptchaHandler(AstGamePlatform
							.getInstance().isDebugMode(), new HttpCallback() {
						public void onSuccess(int code, String msg, Object data) {
							// 成功回调
							Intent intent = new Intent(getApplicationContext(),
									SetGuestBindPhonePasswdActivity.class);
							intent.putExtra("mobile", mobile);
							startActivity(intent);
							finish();
						}

						public void onFailure(int code, String msg, Object data) {
							Toast.makeText(getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();
						}
					}, String.valueOf(AstGamePlatform.getInstance()
							.getAppInfo().getGameId()), mobile, codeText
							.getText().toString()).post();
				} else {
					new ValidateBindCaptchaHandler(AstGamePlatform
							.getInstance().isDebugMode(), new HttpCallback() {
						public void onSuccess(int code, String msg, Object data) {
							// 成功回调
							new AstBindHandler(AstGamePlatform.getInstance()
									.isDebugMode(), new HttpCallback() {
								@Override
								public void onSuccess(int code, String msg,
										Object data) {
									Toast.makeText(getApplicationContext(),
											"绑定成功", Toast.LENGTH_SHORT).show();
									startActivity(new Intent(
											getApplicationContext(),
											WelcomeActivity.class));
									finish();
								}

								@Override
								public void onFailure(int code, String msg,
										Object data) {
									Toast.makeText(getApplicationContext(),
											msg, Toast.LENGTH_SHORT).show();
								}

							}, mobile, String.valueOf(AstGamePlatform
									.getInstance().getAppInfo().getGameId()),
									currUser.getUserName()).post();
						}

						public void onFailure(int code, String msg, Object data) {
							Toast.makeText(getApplicationContext(), msg,
									Toast.LENGTH_SHORT).show();
						}
					}, String.valueOf(AstGamePlatform.getInstance()
							.getAppInfo().getGameId()), mobile, codeText
							.getText().toString()).post();
				}
			} else {
				Toast.makeText(getApplicationContext(), "请输入验证码",
						Toast.LENGTH_SHORT).show();
			}
			return;
		} else if (view.getId() == sendCaptchaAgain.getId()) {
			if (canSendAgain) {
				sendCaptchaAgain.setClickable(false);
				final Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (count > 1) {
									sendCaptchaAgain.setText((--count)
											.toString());
								} else {
									sendCaptchaAgain.setClickable(true);
									sendCaptchaAgain.setText("重试");
									count = 60;
									canSendAgain = true;
									timer.cancel();
								}
							}
						});
					}
				}, 0, 1000);

				new SendBindAccountCaptchaHandler(AstGamePlatform.getInstance()
						.isDebugMode(), new HttpCallback() {
					public void onSuccess(int code, String msg, Object data) {
						// 成功回调
						Toast.makeText(getApplicationContext(), msg,
								Toast.LENGTH_SHORT).show();
					}

					public void onFailure(int code, String msg, Object data) {
						// 失败回调
						Toast.makeText(getApplicationContext(), msg,
								Toast.LENGTH_SHORT).show();
					}
				}, mobile).post();

			}
			return;
		}
		startActivity(intent);
		finish();
	}
}
