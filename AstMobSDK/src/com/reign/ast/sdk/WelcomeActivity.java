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
import android.widget.TextView;

import com.reign.ast.sdk.manager.AstGamePlatform;

public class WelcomeActivity extends Activity implements OnClickListener,
		OnCancelListener {
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		if (AstGamePlatform.getInstance().getUserInfo().isQuickUser()) {
			Intent intent = new Intent();
			intent.putExtra("from", "welcome");
			intent.setClass(this, DeclareBindAccountActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		Dialog dialog = new Dialog(this,
				R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.setContentView(R.layout.ast_mob_sdk_welcome_play);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		if (AstGamePlatform.getInstance().getUserInfo().isQuickUser()) {

			((TextView) dialog.findViewById(R.id.username)).setText("游客");

		} else {
			((TextView) dialog.findViewById(R.id.username))
					.setText(AstGamePlatform.getInstance().getCurrentUser());
		}
		dialog.setOnCancelListener(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
			}
		}, 1000);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancel(DialogInterface arg0) {
		finish();
	}

}
