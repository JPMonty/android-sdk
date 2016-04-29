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
import android.widget.EditText;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.SendRegetPasswdCaptchaHandler;
import com.reign.ast.sdk.http.handler.ValidateRegetPasswdCaptchaHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.ta.utdid2.android.utils.StringUtils;

public class RegetPasswdActivity extends Activity implements OnClickListener {

	Dialog dialog = null;

	Button sendCaptchaAgain = null;
	Boolean canSendAgain = false;
	Integer count = 60;
	EditText codeText = null;

	String mobile = null;

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		mobile = getIntent().getStringExtra("mobile");
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.setContentView(R.layout.ast_mob_sdk_forget_passwd_captcha);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.findViewById(R.id.ast_back_icon).setOnClickListener(this);
		dialog.findViewById(R.id.ast_mob_sdk_next_step_btn).setOnClickListener(
				this);
		codeText = (EditText) dialog.findViewById(R.id.ast_mob_sdk_phone_et);

		sendCaptchaAgain = (Button) dialog.findViewById(R.id.sendAgain);
		sendCaptchaAgain.setClickable(false);

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

	public void onStart() {
		super.onStart();
		new SendRegetPasswdCaptchaHandler(AstGamePlatform.getInstance()
				.isDebugMode(), new HttpCallback() {
			public void onSuccess(int code, String msg, final Object data) {

			}

			/**
			 * 登陆失败处理
			 */
			public void onFailure(int code, final String msg, Object data) {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
				startActivity(new Intent(getApplicationContext(),
						ForgetPasswdActivity.class));
				finish();
			}
		}, mobile).post();

	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		if (view.getId() == R.id.ast_back_icon) {
			intent.setClass(this, BindAccountActivity.class);
		} else if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {

			if (!StringUtils.isEmpty(codeText.getText().toString())) {
				new ValidateRegetPasswdCaptchaHandler(AstGamePlatform
						.getInstance().isDebugMode(), new HttpCallback() {
					public void onSuccess(int code, String msg, Object data) {
						// 成功回调
						Intent intent = new Intent(getApplicationContext(),
								ResetPasswdActivity.class);
						intent.putExtra("mobile", mobile);
						intent.putExtra("key", (String) data);
						startActivity(intent);
						finish();
					}

					public void onFailure(int code, String msg, Object data) {
						Toast.makeText(getApplicationContext(), msg,
								Toast.LENGTH_SHORT).show();
					}
				}, String.valueOf(AstGamePlatform.getInstance().getAppInfo()
						.getGameId()), mobile, codeText.getText().toString())
						.post();
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
				new SendRegetPasswdCaptchaHandler(AstGamePlatform.getInstance()
						.isDebugMode(), new HttpCallback() {
					public void onSuccess(int code, String msg,
							final Object data) {

					}

					/**
					 * 登陆失败处理
					 */
					public void onFailure(int code, final String msg,
							Object data) {
						Toast.makeText(getApplicationContext(), msg,
								Toast.LENGTH_SHORT);
						startActivity(new Intent(getApplicationContext(),
								ForgetPasswdActivity.class));
						finish();
					}
				}, mobile).post();

			}
			return;
		}
		startActivity(intent);
		finish();
	}
}
