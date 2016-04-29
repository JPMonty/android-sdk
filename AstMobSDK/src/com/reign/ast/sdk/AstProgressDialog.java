package com.reign.ast.sdk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reign.ast.sdk.listener.LoginCallbackListener;
import com.reign.ast.sdk.manager.UserManager;

/**
 * 加载 dialog
 * @author zhouwenjia
 *
 */
public class AstProgressDialog extends BaseActivity {
	
	private Dialog dialog;
	private TextView tips;
	
	/** 消息处理handler */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 游客快速登录中
				AstProgressDialog.this.tips.setText(AstProgressDialog.getStringIdByName(AstProgressDialog.this, "ast_mob_sdk_loading_express_id"));
				break;
			case 2:
				// 账号登录中
				AstProgressDialog.this.tips.setText(AstProgressDialog.getStringIdByName(AstProgressDialog.this, "ast_mob_sdk_loading_main_id"));
				break;
			case 3:
				// 关闭自身
				AstProgressDialog.this.finish();
				break;
			}
		}
	};
	
	private static LoginCallbackListener mLoginCallbackListener;

	/**
	 * onCreate
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(getLayoutIdByName(this, "ast_mob_sdk_dialogview"), null);
		LinearLayout layout = (LinearLayout) v.findViewById(getResIdByName(this, "ast_mob_sdk_dialog_view"));
		ImageView spaceshipImage = (ImageView) v.findViewById(getResIdByName(this, "ast_mob_sdk_loading_img"));
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, getAnimIdByName(this, "ast_mob_sdk_loading_animation"));
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		this.dialog = new Dialog(this, getStyleIdByName(this, "ast_mob_sdk_loading_FullHeightDialog"));
		this.dialog.getWindow().setBackgroundDrawableResource(17170445);
		this.dialog.setCancelable(false);
		this.dialog.show();
		this.dialog.setContentView(layout, new LinearLayout.LayoutParams(350, -2));
		this.tips = ((TextView) layout.findViewById(getResIdByName(this, "ast_mob_sdk_loading_info")));
		this.tips.setText(getStringIdByName(this, "ast_mob_sdk_loading_check"));

		UserManager.getInstance().login(this, mLoginCallbackListener, this.mHandler);
	}

	/**
	 * actionLoginActivity
	 * @param ctx
	 * @param loginCallbackListener
	 */
	public static void actionLoginActivity(Context ctx, LoginCallbackListener loginCallbackListener) {
		mLoginCallbackListener = loginCallbackListener;
		Intent intent = new Intent(ctx, AstProgressDialog.class);
		ctx.startActivity(intent);
	}

	/**
	 * destory
	 */
	protected void onDestroy() {
		super.onDestroy();
		if (null != this.dialog) {
			try {
				this.dialog.dismiss();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			this.dialog = null;
		}
	}
}
