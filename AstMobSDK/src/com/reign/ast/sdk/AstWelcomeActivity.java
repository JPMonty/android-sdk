package com.reign.ast.sdk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.reign.ast.sdk.listener.LoginCallbackListener;
import com.reign.ast.sdk.util.DisplayUtil;
import com.reign.ast.sdk.util.Logger;

/**
 * sdk开始页面
 * @author zhouwenjia
 *
 */
public class AstWelcomeActivity extends BaseActivity {

	/** 快速登录button */
	private Button quickLoginBtn;
	
	/** 傲世堂账号button */
	private Button astLoginBtn;
	
	/** dialog */
	private Dialog dialog;
	
	/** login回调 */
	private static LoginCallbackListener mLoginCallbackListener = null;
	
	/**
	 * onCreate
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Logger.d(TAG, "density:" + DisplayUtil.getScale(this));
		View view = LayoutInflater.from(this).inflate(getLayoutIdByName(this, "ast_mob_sdk_welcome"), (ViewGroup) getWindow().getDecorView(), false);
		ViewGroup.LayoutParams vlp = view.getLayoutParams();
		this.dialog = new Dialog(this, getStyleIdByName(this, "ast_mob_sdk_login_FullHeightDialog"));
		this.dialog.getWindow().setBackgroundDrawableResource(17170445);
		this.dialog.setCancelable(false);
		this.dialog.show();
		this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				cancel();
			}
		});
		this.dialog.setContentView(view, vlp);
		this.dialog.setCancelable(true);
		this.dialog.setCanceledOnTouchOutside(true);
		
		this.quickLoginBtn = ((Button) view.findViewById(BaseActivity.getResIdByName(this, "ast_mob_sdk_welcome_quicklogin_btn")));
		this.astLoginBtn = ((Button) view.findViewById(BaseActivity.getResIdByName(this, "ast_mob_sdk_welcome_astlogin_btn")));
		
		this.quickLoginBtn.setClickable(true);
		this.astLoginBtn.setClickable(true);
	}
	
	/**
	 * 打开欢迎页
	 * @param ctx
	 * @param loginCallbackListener
	 */
	public static void actionWelcomeActivity(Context ctx, LoginCallbackListener loginCallbackListener) {
		mLoginCallbackListener = loginCallbackListener;
		Intent intent = new Intent(ctx, AstWelcomeActivity.class);
		ctx.startActivity(intent);
	}
	
	/**
	 * 快速登录
	 * @param v
	 */
	public void quickLogin(View v) {
		Logger.d(TAG, "click quick login button.");
		AstProgressDialog.actionLoginActivity(this, mLoginCallbackListener);
		finish();
	}
	
	/**
	 * 傲世堂账号登录
	 * @param v
	 */
	public void astLogin(View v) {
		AstLoginActivity.login(this, "", mLoginCallbackListener);
		finish();
	}
	
	/**
	 * cancel
	 */
	private void cancel() {
		finish();
	}
	/**
	 * back
	 */
	public void onBackPressed() {
		super.onBackPressed();
		Logger.d(TAG, "onBackPressed");
		cancel();
	}

	protected void onDestroy() {
		super.onDestroy();
	}
}
