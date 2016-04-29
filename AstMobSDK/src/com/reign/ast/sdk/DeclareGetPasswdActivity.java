package com.reign.ast.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class DeclareGetPasswdActivity extends Activity implements
		OnClickListener {

	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		final Dialog dialog = new Dialog(this,
				R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.ast_mob_sdk_forget_passwd_nophone);
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
			intent.setClass(this, ForgetPasswdActivity.class);
		} else if (view.getId() == R.id.ast_mob_sdk_next_step_btn) {
			intent.setClass(this, AstLoginActivity.class);
		}
		finish();
		startActivity(intent);
	}

}
