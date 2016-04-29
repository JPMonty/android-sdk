package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.SendBindAccountCaptchaHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.pojo.UserInfo;

public class BindAccountActivity extends Activity implements OnClickListener {

	EditText mobile = null;
	UserInfo currUser = null;

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		final Dialog dialog = new Dialog(this,
				R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.show();
		dialog.setContentView(R.layout.ast_mob_sdk_phone_bind);
		dialog.findViewById(R.id.ast_back_icon).setOnClickListener(this);
		dialog.findViewById(R.id.ast_mob_sdk_nextstep_btn).setOnClickListener(
				this);
		currUser = AstGamePlatform.getInstance().getUserInfo();
		dialog.findViewById(R.id.login_bind).setOnClickListener(this);
		dialog.findViewById(R.id.regist_bind).setOnClickListener(this);

		if (!currUser.isQuickUser()) {
			dialog.findViewById(R.id.login_bind).setVisibility(View.GONE);
			dialog.findViewById(R.id.regist_bind).setVisibility(View.GONE);
		}
		mobile = (EditText) dialog.findViewById(R.id.ast_mob_sdk_phone_et);
		dialog.findViewById(R.id.clear).setOnClickListener(this);
		
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
			if (getIntent().getBooleanExtra("userCenter", false)) {
				intent.setClass(this, AccountUserCenterActivity.class);
			} else {
				intent.setClass(this, DeclareBindAccountActivity.class);
			}
		} else if (view.getId() == R.id.ast_mob_sdk_nextstep_btn) {
			if (!mobile.getText().toString()
					.matches("(13[0-9]|14[0-9]|15[0-9]|18[0-9])\\d{8}")) {
				Toast.makeText(getApplicationContext(), "手机号码不符合规则",
						Toast.LENGTH_LONG).show();
				return;
			}
			new SendBindAccountCaptchaHandler(AstGamePlatform.getInstance()
					.isDebugMode(), new HttpCallback() {
				public void onSuccess(int code, String msg, Object data) {
					// 成功回调
					super.onSuccess(code, msg, data);
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("mobile", mobile.getText().toString());
					intent.setClass(getApplicationContext(),
							BindTypeCaptchaActivity.class);
					startActivity(intent);
					finish();
				}

				public void onFailure(int code, String msg, Object data) {
					// 失败回调
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_SHORT).show();
				}
			}, mobile.getText().toString()).post();

			return;
		} else if (view.getId() == R.id.clear) {
			mobile.setText("");
			return;
		} else if (view.getId() == R.id.login_bind) {

			startActivity(new Intent(this, LoginBindActivity.class));
			finish();
			return;
		} else if (view.getId() == R.id.regist_bind) {
			startActivity(new Intent(this, RegistBindActivity.class));
			finish();
			return;
		}
		finish();
		startActivity(intent);
	}

}
