package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.GuestBindHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;

public class SetGuestBindPhonePasswdActivity extends Activity implements
		OnClickListener {

	Dialog dialog = null;

	EditText password1 = null;

	EditText password2 = null;

	Button regist = null;

	String mobile = null;

	final String TAG = getClass().getSimpleName();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);
		
		dialog.setContentView(R.layout.ast_mob_sdk_set_phone_passwd);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		
		password1 = (EditText) dialog.findViewById(R.id.ast_mob_sdk_new_passwd);

		password2 = (EditText) dialog
				.findViewById(R.id.ast_mob_sdk_new_passwd_again);

		regist = (Button) dialog.findViewById(R.id.ast_mob_sdk_next_step_btn);

		regist.setOnClickListener(this);

		mobile = getIntent().getStringExtra("mobile");

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == regist.getId()) {
			if (!password1.getText().toString().matches(".{6,20}")) {
				Toast.makeText(getApplicationContext(), "密码不符合规则",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (!password1.getText().toString()
					.equals(password2.getText().toString())) {
				Toast.makeText(getApplicationContext(), "两次密码输入不一致",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (AstGamePlatform
					.getInstance().getUserInfo().isQuickUser()) {
				new GuestBindHandler(AstGamePlatform.getInstance().isDebugMode(),
						new HttpCallback() {
					
							@Override
							public void onSuccess(int code, String msg, Object data) {
								// 登陆处理成功
								Logger.d(TAG, "phone bind success.");
								UserInfo userInfo = (UserInfo) data;
								
								final GameAccount gameAccount = new GameAccount(
										GameAccount.PHONE, mobile, password1
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

								GameUtil.loginTypeMark(getApplicationContext(),
										GameAccount.PHONE);

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

						}, String.valueOf(AstGamePlatform.getInstance()
								.getAppInfo().getGameId()), mobile, password1
								.getText().toString(), AstGamePlatform
								.getInstance().getUserInfo().getTmpPlayerId())
						.post();
				
			}
		
			return;
		}
	}

}
