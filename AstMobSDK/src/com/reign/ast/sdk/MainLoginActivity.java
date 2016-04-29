package com.reign.ast.sdk;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.util.GameUtil;

public class MainLoginActivity extends Activity implements OnClickListener {

	private Dialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);

		List<GameAccount> users = GameUtil.getUserHistory(this);
		if (users != null && users.size() > 0) {
			UserManager.chooseToLogin(users, this, null, null, true);
			finish();
			return;
		}
		// dialog.setCanceledOnTouchOutside(true);
		View view = LayoutInflater.from(this).inflate(
				R.layout.ast_mob_sdk_main,
				(ViewGroup) getWindow().getDecorView(), false);
		dialog.setContentView(view);
		dialog.setCancelable(false);
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
			}
		});
		dialog.findViewById(R.id.visitor_account).setOnClickListener(this);
		dialog.findViewById(R.id.ast_account).setOnClickListener(this);
		dialog.findViewById(R.id.phone_account).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		Intent intent = null;
		if (view.getId() == R.id.visitor_account) {
			intent = new Intent(this, DeclareBindAccountActivity.class);
			// dialog.setContentView(R.layout.ast_mob_sdk_bind_account);
		} else if (view.getId() == R.id.ast_account) {
			finish();
			AstLoginActivity.startAstLoginActivity(this, AstGamePlatform
					.getInstance().getSwitchUserListener());
			return;
		} else if (view.getId() == R.id.phone_account) {
			intent = new Intent(this, PhoneLoginActivity.class);
		}

		if (intent != null) {
			finish();
			startActivity(intent);
		}
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// show();
	// }
	//
	// public void hide() {
	// dialog.hide();
	// }
	//
	// public void show() {
	// dialog.show();
	// }

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		System.out.println("按下了back键   onBackPressed()");
	}

}
