package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.SendPhoneRegisterCaptchaHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;

public class PhoneLoginActivity extends Activity implements OnClickListener {
	private Dialog dialog = null;

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.ast_mob_sdk_phone_login);
		dialog.findViewById(R.id.ast_back_icon).setOnClickListener(this);
		dialog.findViewById(R.id.ast_mob_sdk_next_step_btn).setOnClickListener(
				this);
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		dialog.findViewById(R.id.text2).setOnClickListener(this);
		
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
		Intent intent = new Intent();
		if (view.getId() == R.id.ast_back_icon) {
			intent.setClass(this, MainLoginActivity.class);
		} else if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {
			final String phoneNumber = ((TextView) dialog
					.findViewById(R.id.ast_mob_sdk_phone_et)).getText()
					.toString();
			if (!phoneNumber.matches("(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}")) {
				Toast.makeText(getApplicationContext(), "手机号码不符合规则",
						Toast.LENGTH_LONG).show();
				return;
			}

			new SendPhoneRegisterCaptchaHandler(AstGamePlatform.getInstance()
					.isDebugMode(), new HttpCallback() {
				public void onSuccess(int code, String msg, Object data) {
					// 成功回调
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("phoneNumber", phoneNumber);
					intent.setClass(getApplicationContext(),
							PhoneRegisterTypeCaptchaActivity.class);
					startActivity(intent);
					finish();
				}

				public void onFailure(int code, String msg, Object data) {
					// 失败回调

					if (code == 317) {
						Toast.makeText(getApplicationContext(), "该手机号已被绑定，请直接登录",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra("phoneNumber", phoneNumber);
						intent.setClass(getApplicationContext(),
								AstLoginActivity.class);
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), msg,
								Toast.LENGTH_SHORT).show();
					}
				}
			}, phoneNumber).post();
			return;
		} else if (view.getId() == R.id.text2) {
			intent.setClass(this, RegisterAstActivity.class);
		}
		finish();
		startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK ) { //按下的如果是BACK，同时没有重复

	return false;
	}

	return super.onKeyDown(keyCode, event);
	}

	
	

}
