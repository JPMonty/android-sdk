package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.reign.ast.sdk.http.HttpCallback;
import com.reign.ast.sdk.http.handler.ResetPasswordHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;

public class ResetPasswdActivity extends Activity implements OnClickListener {
	String mobile = null;
	String key;
	Dialog dialog;
	EditText passwd1;
	EditText passwd2;

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		Intent intent = getIntent();
		mobile = intent.getStringExtra("mobile");
		key = intent.getStringExtra("key");
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.setContentView(R.layout.ast_mob_sdk_reset_passwd);
		dialog.show();
		passwd1 = (EditText) dialog.findViewById(R.id.ast_mob_sdk_new_passwd);
		passwd2 = (EditText) dialog
				.findViewById(R.id.ast_mob_sdk_new_passwd_again);

		dialog.findViewById(R.id.ast_mob_sdk_next_step_btn).setOnClickListener(
				this);

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {
			final String passwd1 = this.passwd1.getText().toString();
			String passwd2 = this.passwd2.getText().toString();

			if (!passwd1.matches(".{6,20}")) {
				Toast.makeText(getApplicationContext(), "密码不符合规则",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!passwd1.equals(passwd2)) {
				Toast.makeText(getApplicationContext(), "两次密码输入不一致",
						Toast.LENGTH_SHORT).show();
				return;
			}

			new ResetPasswordHandler(AstGamePlatform.getInstance()
					.isDebugMode(), new HttpCallback() {
				public void onSuccess(int code, String msg, final Object data) {
					startActivity(new Intent(getApplicationContext(),
							AstLoginActivity.class));
					Toast.makeText(getApplicationContext(), "密码重置成功",
							Toast.LENGTH_LONG).show();
					finish();
				}

				/**
				 * 登陆失败处理
				 */
				public void onFailure(int code, final String msg, Object data) {
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_SHORT).show();
				}
			}, mobile, passwd1, key, String.valueOf(AstGamePlatform
					.getInstance().getAppInfo().getGameId())).post();

		}

	}
}
