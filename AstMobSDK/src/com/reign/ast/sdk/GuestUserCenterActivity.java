package com.reign.ast.sdk;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;

public class GuestUserCenterActivity extends Activity implements
		OnClickListener {

	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dialog = new Dialog(this, R.style.ast_mob_sdk_loading_FullHeightDialog);
		dialog.setContentView(R.layout.ast_mob_sdk_guest_detail);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		dialog.show();
		if (AstGamePlatform.getInstance().getUserInfo().isQuickUser()) {
			((TextView) dialog.findViewById(R.id.username)).setText("游客");
		} else {
			((TextView) dialog.findViewById(R.id.username))
					.setText(AstGamePlatform.getInstance().getCurrentUser());
		}
		
		dialog.findViewById(R.id.ast_mob_sdk_change_user_btn)
				.setOnClickListener(this);
		dialog.findViewById(R.id.ast_mob_sdk_safe_btn).setOnClickListener(this);
		dialog.findViewById(R.id.close).setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.ast_mob_sdk_change_user_btn) {
			List<GameAccount> users = GameUtil
					.getUserHistory(getApplicationContext());
			if (users == null || users.size() == 0) {
				Toast.makeText(getApplicationContext(), "您尚未登陆过该应用!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			final UserInfo userInfo = AstGamePlatform.getInstance()
					.getUserInfo();
			if (userInfo != null) {
				Collections.sort(users, new Comparator<GameAccount>() {
					@Override
					public int compare(GameAccount a, GameAccount b) {
						if (userInfo.getUserName().equals(a.name))
							return -1;
						if (userInfo.getUserName().equals(b.name))
							return 1;
						return 0;
					}
				});
			}
			UserManager.chooseToLogin(users, getApplicationContext(), null,
					null, true);
			finish();
		} else if (view.getId() == R.id.ast_mob_sdk_safe_btn) {
			startActivity(new Intent(this, BindAccountActivity.class));
			finish();
		} else if (view.getId() == R.id.close) {
			finish();
		}
	}

}
