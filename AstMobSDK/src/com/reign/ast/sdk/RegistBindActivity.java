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
import com.reign.ast.sdk.http.handler.RegistBindHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;

public class RegistBindActivity extends Activity implements OnClickListener {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);

		dialog.show();

		dialog.setContentView(R.layout.ast_mob_sdk_register_bind);

		dialog.findViewById(R.id.ast_back_icon).setOnClickListener(
				new android.view.View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						startActivity(new Intent(RegistBindActivity.this,
								BindAccountActivity.class));
						finish();
					}
				});

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
			intent.setClass(this, BindAccountActivity.class);
		} else if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {
			if (!agree.isChecked()) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = "请先同意隐私协议";
				mhanler.sendMessage(msg);
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

				new RegistBindHandler(AstGamePlatform.getInstance()
						.isDebugMode(), new HttpCallback() {

					@Override
					public void onSuccess(int code, String msg, Object data) {
						// 登陆处理成功
						UserInfo userInfo = (UserInfo) data;

						final GameAccount gameAccount = new GameAccount(
								GameAccount.COMMON, account.getText()
										.toString(), password1.getText()
										.toString(), userInfo.getToken());
						// 用户信息持久化
						GameUtil.saveAccountInfo(getApplicationContext(),
								gameAccount);

						GameUtil.removeGuest(getApplicationContext());

						// 设置用户信息
						AstGamePlatform.getInstance().setUserInfo(
								userInfo.setUserName(gameAccount.name));
						// 刷新用户
						UserManager.refreshUser(userInfo);

						GameUtil.loginTypeMark(getApplicationContext(),
								GameAccount.COMMON);

						AstGamePlatform.getInstance().getSwitchUserListener()
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

				},
						AstGamePlatform.getInstance().getUserInfo()
								.getTmpPlayerId(),
						account.getText().toString(), password1.getText()
								.toString()).post();

			}
			return;
		} 
		finish();
		startActivity(intent);
	}

}
