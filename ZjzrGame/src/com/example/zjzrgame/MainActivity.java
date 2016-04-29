package com.example.zjzrgame;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.reign.ast.sdk.AccountUserCenterActivity;
import com.reign.ast.sdk.GuestUserCenterActivity;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.UserManager;
import com.reign.ast.sdk.pojo.GameAccount;
import com.reign.ast.sdk.pojo.UserInfo;
import com.reign.ast.sdk.util.GameUtil;
import com.reign.ast.sdk.util.Logger;
import com.reign.sdk.Ast;

public class MainActivity extends Activity {
	static final String TAG = MainActivity.class.getSimpleName();
	Ast ast = new Ast();

	private volatile boolean inited = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "init...");
		if (!inited) {
			ast.initSDK(this, this);
			this.inited = true;
		}
		Log.i(TAG, "user login...");
		ast.userLogin();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void progress() {

	}

	public void login(View v) {
		Log.i(TAG, "init...");
		if (!inited) {
			ast.initSDK(this, this);
			this.inited = true;
		}
		Log.i(TAG, "user login...");
		ast.userLogin();
	}

	public void exchange_user(View v) {
		System.out.println("exchange_user");

		List<GameAccount> users = GameUtil
				.getUserHistory(getApplicationContext());
		if (users == null || users.size() == 0) {
			Toast.makeText(getApplicationContext(), "ÄúÉÐÎ´µÇÂ½¹ý¸ÃÓ¦ÓÃ!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		final UserInfo userInfo = AstGamePlatform.getInstance().getUserInfo();
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
		Logger.d("MainActivity", "chooseToLoginÕËºÅµÇÂ¼.");
		UserManager.chooseToLogin(users, getApplicationContext(), null, null, false);

	}

	public void user_center(View v) {
		System.out.println("user_center");
		final UserInfo userInfo = AstGamePlatform.getInstance().getUserInfo();
		if (userInfo == null) {
			Toast.makeText(getApplicationContext(), "ÇëÏÈµÇÂ¼", Toast.LENGTH_SHORT).show();
			return;
		}
		if (userInfo.isQuickUser()) {
			startActivity(new Intent(this, GuestUserCenterActivity.class));
		} else {
			startActivity(new Intent(this, AccountUserCenterActivity.class));
		}

	}
}
