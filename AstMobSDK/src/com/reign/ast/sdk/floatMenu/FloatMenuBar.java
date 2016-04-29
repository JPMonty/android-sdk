package com.reign.ast.sdk.floatMenu;

//import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.reign.ast.sdk.BaseActivity;
import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.manager.FloatMenuManager;
import com.reign.ast.sdk.pojo.UserInfo;

/**
 * 浮动菜单条
 * 
 * @author zhouwenjia
 * 
 */
public class FloatMenuBar extends LinearLayout {
	private int viewWidth;
	private int viewHeight;

	private WindowManager windowManager;
	private WindowManager.LayoutParams mParams;

	private Context mContext;
	private FloatMenuManager mFloatMenuManager;
	private OnClickListener mSwitchUser = new OnClickListener() {
		public void onClick(View v) {
			mFloatMenuManager.hideMenuBar();
			AstGamePlatform.getInstance().switchUser(mContext, null);
		}
	};

	private OnClickListener mCustomerService = new OnClickListener() {
		public void onClick(View v) {
			mFloatMenuManager.hideMenuBar();
//			AstGamePlatform.getInstance().goFeedBack(mContext);
		}
	};

	private OnClickListener mPerfectAccount = new OnClickListener() {
		public void onClick(View v) {
			mFloatMenuManager.hideMenuBar();
			AstGamePlatform.getInstance().perfectAccount(mContext, null);
		}
	};

	public int getViewWidth() {
		return this.viewWidth;
	}

	public int getViewHeight() {
		return this.viewHeight;
	}

	/**
	 * 构造函数
	 * @param floatMenuManager
	 * @param context
	 * @param isFullscreen
	 */
	public FloatMenuBar(FloatMenuManager floatMenuManager, Context context, boolean isFullScreen) {
		super(context);
		this.mContext = context;
		this.mFloatMenuManager = floatMenuManager;
		this.windowManager = ((WindowManager) context.getSystemService("window"));
		UserInfo user = AstGamePlatform.getInstance().getUserInfo();
		View view = null;
		if (user.isQuickUser()) {
			view = LayoutInflater.from(context).inflate(BaseActivity.getLayoutIdByName(context, "ast_mob_sdk_float_menu"), this);
			view = view.findViewById(BaseActivity.getResIdByName(context, "ast_mob_sdk_float_menu_bg"));
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(lp);
			view.findViewById(BaseActivity.getResIdByName(context, "ast_mob_sdk_float_menu_switch_user_rl")).setOnClickListener(this.mSwitchUser);
			view.findViewById(BaseActivity.getResIdByName(context, "ast_mob_sdk_float_menu_customer_service_rl")).setOnClickListener(this.mCustomerService);
			view.findViewById(BaseActivity.getResIdByName(context, "ast_mob_sdk_float_menu_perfect_account_rl")).setOnClickListener(this.mPerfectAccount);
		} else {
			view = LayoutInflater.from(context).inflate(BaseActivity.getLayoutIdByName(context, "ast_mob_sdk_float_menu2"), this);
			view = view.findViewById(BaseActivity.getResIdByName(context, "ast_mob_sdk_float_menu_bg"));
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(lp);
			view.findViewById(BaseActivity.getResIdByName(context, "ast_mob_sdk_float_menu_switch_user_rl")).setOnClickListener(this.mSwitchUser);
			view.findViewById(BaseActivity.getResIdByName(context, "ast_mob_sdk_float_menu_customer_service_rl")).setOnClickListener(this.mCustomerService);
		}
		this.viewWidth = view.getLayoutParams().width;
		this.viewHeight = view.getLayoutParams().height;
	}

	/**
	 * 构造函数
	 * @param context
	 * @param config
	 * @param isFullscreen
	 */
	public FloatMenuBar(Context context, List<String> config, boolean isFullscreen) {
		super(context);
	}

	/**
	 * onTouchEvent
	 */
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
	}

	public void setParams(WindowManager.LayoutParams params) {
		this.mParams = params;
	}

	/**
	 * 更新位置
	 * @param x
	 * @param y
	 */
	public void updateViewPosition(int x, int y) {
		this.mParams.x = x;
		this.mParams.y = y;
		this.windowManager.updateViewLayout(this, this.mParams);
	}

	/**
	 * 悬浮bar清理操作
	 */
	public void clear() {
		// TODO
	}
}
