package com.reign.ast.sdk.manager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import com.reign.ast.sdk.floatMenu.FloatMenuBar;
import com.reign.ast.sdk.floatMenu.FloatMenuIcon;
import com.reign.ast.sdk.listener.FloatMenuIconListener;
import com.reign.ast.sdk.pojo.UserInfo;

/**
 * 浮动菜单
 * @author zhouwenjia
 * 
 */
public class FloatMenuManager implements FloatMenuIconListener {
	private static final String TAG = FloatMenuManager.class.getSimpleName();
	private Context mContext;
	private FloatMenuIcon mFloatMenuIcon;
	private FloatMenuBar mFloatMenuBar;
	private WindowManager.LayoutParams floatMenuIconParams;
	private WindowManager.LayoutParams floatMenuBarParams;
	private WindowManager mWindowManager;
	private boolean isIconShow;
	private boolean isBarShow;
	private int x = -1;
	private int y = -1;
	private boolean isRefresh;
	boolean isQuickUser;
	int screenWidth;
	int screenHeight;
	boolean isFullScreen;
	boolean isShow;
	private AstGamePlatform mAstGamePlatform = AstGamePlatform.getInstance();

	/**
	 * 构造函数
	 * @param context
	 * @param isFullscreen
	 */
	public FloatMenuManager(Context context, boolean isFullScreen) {
		this.mContext = context;
		this.isFullScreen = isFullScreen;
	}

	/**
	 * 设置参数xy
	 * @param x
	 * @param y
	 */
	public void setParamsXY(int x, int y) {
		if ((this.x != x) || (this.y != y)) {
			this.isRefresh = true;
		} else {
			this.isRefresh = false;
		}
		this.x = x;
		this.y = y;
	}
	
	/**
	 * 充值menuBar
	 */
	public void resetMenuBar() {
		UserInfo userInfo = this.mAstGamePlatform.getUserInfo();
		if (null == userInfo) {
			return;
		}
		boolean isQuickUser = userInfo.isQuickUser();
		createMenuBar(isQuickUser);
	}

	/**
	 * 创建menu icon
	 */
	@SuppressWarnings("deprecation")
	private void createMenuIcon() {
		int screenWidth = this.mWindowManager.getDefaultDisplay().getWidth();
//		int screenHeight = this.mWindowManager.getDefaultDisplay().getHeight();
		if ((null == this.mFloatMenuIcon) || (this.isRefresh)) {
			this.mFloatMenuIcon = new FloatMenuIcon(this.mContext, this.isFullScreen);
			this.mFloatMenuIcon.setListener(this);
			if (this.floatMenuIconParams == null) {
				this.floatMenuIconParams = new WindowManager.LayoutParams();
				this.floatMenuIconParams.type = 64;
				this.floatMenuIconParams.format = 1;
				this.floatMenuIconParams.flags = 40;
				this.floatMenuIconParams.gravity = 51;
				this.floatMenuIconParams.width = this.mFloatMenuIcon.getViewWidth();
				this.floatMenuIconParams.height = this.mFloatMenuIcon.getViewHeight();
				if (this.x != -1) {
					this.floatMenuIconParams.x = (screenWidth - this.x);
				} else {
//					this.floatMenuIconParams.x = (screenWidth - 10);
					this.floatMenuIconParams.x = 0;
				}
				if (this.y != -1) {
					this.floatMenuIconParams.y = this.y;
				} else {
//					this.floatMenuIconParams.y = (screenHeight / 2 - 20);
					this.floatMenuIconParams.y = 0;
				}
			}
			this.mFloatMenuIcon.setParams(this.floatMenuIconParams);
		}
	}

	/**
	 * 显示菜单icon
	 */
	private void showMenuIcon() {
		if ((this.mFloatMenuIcon.getParent() == null) && (!((Activity) this.mContext).isFinishing())) {
			this.mWindowManager.addView(this.mFloatMenuIcon, this.floatMenuIconParams);
			this.isIconShow = true;
		}
	}

	/**
	 * 隐藏菜单icon
	 */
	private void hideMenuIcon() {
		if ((this.isIconShow) && (this.mFloatMenuIcon.getParent() != null)) {
			this.mFloatMenuIcon.clear();
			this.mWindowManager.removeViewImmediate(this.mFloatMenuIcon);
//			this.mWindowManager.removeView(this.mFloatMenuIcon);
			this.isIconShow = false;
		}
	}

	/**
	 * 创建菜单条
	 * @param isQuickUser
	 */
	@SuppressWarnings("deprecation")
	private void createMenuBar(boolean isQuickUser) {
		int screenWidth = this.mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = this.mWindowManager.getDefaultDisplay().getHeight();
		if ((null == this.mFloatMenuBar) || (isQuickUser != this.isQuickUser)) {
			this.mFloatMenuBar = new FloatMenuBar(this, this.mContext, this.isFullScreen);
			this.isQuickUser = isQuickUser;
			if (null == this.floatMenuBarParams) {
				this.floatMenuBarParams = new WindowManager.LayoutParams();
				this.floatMenuBarParams.x = (screenWidth / 2 - this.mFloatMenuBar.getViewWidth() / 2);
				this.floatMenuBarParams.y = (screenHeight / 2 - this.mFloatMenuBar.getViewHeight() / 2) - 30;
				this.floatMenuBarParams.flags = 40;

				this.floatMenuBarParams.type = 64;
				this.floatMenuBarParams.format = 1;
				this.floatMenuBarParams.gravity = 51;
				this.floatMenuBarParams.width = this.mFloatMenuBar.getViewWidth();
				this.floatMenuBarParams.height = this.mFloatMenuBar.getViewHeight();
			}
			this.mFloatMenuBar.setParams(this.floatMenuBarParams);
		}
	}

	/**
	 * 显示菜单条
	 */
	private void showMenuBar() {
		Log.d(TAG, "showMenuBar");
		if ((this.mFloatMenuBar != null) && (this.mFloatMenuBar.getParent() == null) && (!((Activity) this.mContext).isFinishing())) {
			this.mWindowManager.addView(this.mFloatMenuBar, this.floatMenuBarParams);
			this.mFloatMenuBar.setAnimation(AnimationUtils.loadAnimation(this.mContext, 17432578));
			this.isBarShow = true;
		}
	}

	/**
	 * 隐藏菜单条
	 */
	public void hideMenuBar() {
		if ((this.mFloatMenuBar != null) && (this.mFloatMenuBar.getParent() != null)) {
			this.mFloatMenuBar.clear();
			this.mWindowManager.removeView(this.mFloatMenuBar);
			this.mFloatMenuBar.setAnimation(AnimationUtils.loadAnimation(this.mContext, 17432576));
			this.isBarShow = false;
		}
	}

	/**
	 * 更新菜单条位置
	 * @param x
	 * @param y
	 */
	private void updateMenuBarPosition(int x, int y) {
		if ((null != this.mFloatMenuBar) && (null != this.floatMenuBarParams)) {
			this.mFloatMenuBar.updateViewPosition(x, y);
		}
	}

	/**
	 * 更新菜单icon位置
	 * @param x
	 * @param y
	 */
	private void updateMenuIconPosition(int x, int y) {
		if ((null != this.mFloatMenuIcon) && (null != this.floatMenuIconParams)) {
			this.mFloatMenuIcon.updateViewPosition(x, y);
		}
	}

	/**
	 * 单击
	 */
	public void onClick(float x, float y) {
		Log.d(TAG, "onClick..x:" + x + " y:" + y + " isBarShow:" + this.isBarShow);
		Log.d(TAG, "FloatMenuBar Width:" + this.mFloatMenuBar.getWidth() + " Height:" + this.mFloatMenuBar.getHeight());
		Log.d(TAG, "FloatMenuIcon Width:" + this.mFloatMenuIcon.getWidth()
				+ " Height:" + this.mFloatMenuIcon.getHeight() + " screenWidth:" + this.screenWidth);
		if (!this.isBarShow) {
			// 显示菜单条
			showMenuBar();

			if (x > this.screenWidth / 2) {
				updateMenuBarPosition((int) x - this.mFloatMenuBar.getWidth(), (int) y + 5);
				Log.d(TAG, "Menu icon on the left");
			} else {
				updateMenuBarPosition((int) x + this.mFloatMenuIcon.getWidth(), (int) y + 5);
				Log.d(TAG, "Menu icon on the right");
				int i = (int) (x + this.mFloatMenuBar.getWidth() + this.mFloatMenuIcon.getWidth() - this.screenWidth);
				Log.d(TAG, "Menu icon on the right i:" + i);
				if (i > 0) {
					updateMenuIconPosition((int) x - i - 5, (int) y);
					updateMenuBarPosition((int) x - i - 5 + this.mFloatMenuIcon.getWidth(), (int) y + 5);
				}
			}
		} else {
			// 隐藏菜单条
			hideMenuBar();
		}
	}

	/**
	 * 移动
	 */
	public void onMove() {
		hideMenuBar();
	}

	/**
	 * 显示
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		UserInfo mUserInfo = this.mAstGamePlatform.getUserInfo();
		if (null == mUserInfo) {
			// Toast.makeText(this.mContext, "请先登录账号", Toast.LENGTH_LONG).show();
			return;
		}
		boolean isQuickUser = mUserInfo.isQuickUser();

		if (!this.isShow) {
			if (this.mWindowManager == null) {
				this.mWindowManager = ((WindowManager) this.mContext.getSystemService("window"));
			}
			// 
			this.screenWidth = this.mWindowManager.getDefaultDisplay().getWidth();
			this.screenHeight = this.mWindowManager.getDefaultDisplay().getHeight();
			hideMenuIcon();
			createMenuIcon();
			showMenuIcon();
			createMenuBar(isQuickUser);
			showMenuBar();
			hideMenuBar();
			this.isShow = true;
		}
	}

	/**
	 * 隐藏
	 */
	public void hide() {
		if (this.isShow) {
			if (null != this.mContext) {
				hideMenuIcon();
				hideMenuBar();
			}
			this.isShow = false;
		}
	}
}
