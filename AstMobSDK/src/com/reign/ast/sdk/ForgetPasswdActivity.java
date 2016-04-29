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
import com.reign.ast.sdk.http.handler.GuestPhoneCanBindHandler;
import com.reign.ast.sdk.manager.AstGamePlatform;

public class ForgetPasswdActivity extends Activity implements OnClickListener {
	EditText ed = null;

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		final Dialog dialog = new Dialog(this,
				R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.setContentView(R.layout.ast_mob_sdk_forget_passwd);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.findViewById(R.id.ast_back_icon).setOnClickListener(this);
		dialog.findViewById(R.id.ast_mob_sdk_next_step_btn).setOnClickListener(
				this);
		ed = (EditText) dialog.findViewById(R.id.ast_mob_sdk_phone_et);
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
			intent.setClass(this, AstLoginActivity.class);
		} else if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {
			new GuestPhoneCanBindHandler(AstGamePlatform.getInstance()
					.isDebugMode(), new HttpCallback() {
				public void onSuccess(int code, String msg, final Object data) {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(),
							DeclareGetPasswdActivity.class);
					startActivity(intent);
					finish();
				}

				/**
				 * 登陆失败处理
				 */
				public void onFailure(int code, final String msg, Object data) {
					if (code != 317) {
						Toast.makeText(getApplicationContext(), msg,
								Toast.LENGTH_LONG).show();
					} else {
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(),
								RegetPasswdActivity.class);
						intent.putExtra("mobile", ed.getText().toString());
						startActivity(intent);
						finish();
					}
				}
			}, ed.getText().toString()).post();
			return;
		}
		finish();
		startActivity(intent);
	}
}
