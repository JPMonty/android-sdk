package com.reign.ast.sdk.floatMenu;

import java.lang.reflect.Field;
import java.util.Timer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.reign.ast.sdk.BaseActivity;
import com.reign.ast.sdk.listener.FloatMenuIconListener;
import com.reign.ast.sdk.task.FloatIconHalfHideTask;

/**
 * 浮动菜单icon
 * 
 * @author zhouwenjia
 * 
 */
public class FloatMenuIcon extends LinearLayout {
	private static final String TAG = FloatMenuIcon.class.getSimpleName();
	public static final int MENU_RIGHT_TYPE = 1;
	public static final int MENU_RIGHT_LEFT = 2;
	private int viewWidth;
	private int viewHeight;
	private int statusBarHeight;
	private WindowManager windowManager;
	private WindowManager.LayoutParams mParams;
	private View iconView;
	private float iconScale = 1.0f;
	private float xInScreen;
	private float yInScreen;
	private float xDownInScreen;
	private float yDownInScreen;
	private float xInView;
	private float yInView;
	private boolean isFullscreen;
	private FloatMenuIconListener mFloatMenuIconListener;
	private Timer timer;
//	private boolean isLongClickModule = false;
	
	/** handler */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 悬浮icon缩小
				updateViewScale();
				if (null != mFloatMenuIconListener) {
					mFloatMenuIconListener.onMove();
				}
				updateViewPosition();
				break;
			default:;
			}
		}
	};

	/**
	 * 构造函数
	 * @param context
	 * @param isFullscreen
	 */
	public FloatMenuIcon(Context context, boolean isFullscreen) {
		super(context);
		this.isFullscreen = isFullscreen;
		windowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
		
		View view = LayoutInflater.from(context).inflate(BaseActivity.getLayoutIdByName(context, "ast_mob_sdk_float_menu_icon"), this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
		view.setLayoutParams(lp);
		view.setOnLongClickListener(new View.OnLongClickListener() {
			// 长按事件监听
			public boolean onLongClick(View v) {
				Log.i(TAG, "onLongClick");
				return false;
			}
		});
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		
		this.xInScreen = 0;
		this.yInScreen = 0;
		this.iconView = view;
		this.iconScale = 0.5f;
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
		// 定时3秒后半隐藏悬浮icon
		timer = new Timer();
		timer.schedule(new FloatIconHalfHideTask(this, true), 5000l);
	}

	/**
	 * 设置icon监听
	 * @param listener
	 */
	public void setListener(FloatMenuIconListener listener) {
		mFloatMenuIconListener = listener;
	}

	/**
	 * onTouchEvent
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("FloatMenuView", "onTouchEvent");
		boolean leftAlign = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 按下
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = (event.getRawY() - getStatusBarHeight());
			xInScreen = event.getRawX();
			yInScreen = (event.getRawY() - getStatusBarHeight());
//			isLongClickModule = true;

			if (null != timer) {
				timer.cancel();
				timer = null;
			}
			if (iconScale < 1.0f) {
				iconScale = 1.0f;
				updateViewScale();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// 移动
			xInScreen = event.getRawX();
			yInScreen = (event.getRawY() - getStatusBarHeight());

			updateViewPosition();
			if (null != mFloatMenuIconListener) {
				mFloatMenuIconListener.onMove();
			}
			double deltaX = Math.sqrt((event.getX() - xInView) * (event.getX() - xInView) + (event.getY() - yInView) * (event.getY() - yInView));
			if ((deltaX > 10.0d) && (null != timer)) {
				timer.cancel();
				timer = null;
			}
			break;
		case MotionEvent.ACTION_UP:
			// up
//			isLongClickModule = false;
			
			double limit = 5.0d;
			if ((Math.abs(xDownInScreen - xInScreen)) <= limit && Math.abs(yDownInScreen - yInScreen) <= limit) {
				// 容忍的移动距离5以内
				if (null != mFloatMenuIconListener) {
					mFloatMenuIconListener.onClick(xInScreen - xInView, yInScreen - yInView);
				}
				int screenWidth = windowManager.getDefaultDisplay().getWidth();
				if (xInScreen < screenWidth / 2) {
					leftAlign = true;
				} else {
					leftAlign = false;
				}
			} else {
				int screenWidth = windowManager.getDefaultDisplay().getWidth();
				if (xInScreen < screenWidth / 2) {
					xInScreen = xInView;
					xInScreen = -200;
					leftAlign = true;
				} else {
					xInScreen = screenWidth;
					leftAlign = false;
				}
				updateViewPosition();
			}
			if (null != timer) {
				timer.cancel();
				timer = null;
			}
			// 定时3秒后半隐藏悬浮icon
			timer = new Timer();
			timer.schedule(new FloatIconHalfHideTask(this, leftAlign), 3000l);
			break;
		}

		return true;
	}

	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}
	
	/**
	 * 半隐藏悬浮icon
	 * @param leftAlign
	 */
	@SuppressWarnings("deprecation")
	public void halfHideFloatIcon(boolean leftAlign) {
		if (leftAlign) {
			xInScreen = 0;
		} else {
			xInScreen = windowManager.getDefaultDisplay().getWidth() + xInView;
		}
		this.iconScale = 0.5f;
		mHandler.sendEmptyMessage(1);
	}
	
	/**
	 * 更新view的缩放大小
	 */
	public void updateViewScale() {
		if (null != iconView) {
			iconView.setScaleX(iconScale);
			iconView.setScaleY(iconScale);
		}
	}

	/**
	 * 更新view位置
	 */
	public void updateViewPosition() {
		mParams.x = ((int) (xInScreen - xInView));
		mParams.y = ((int) (yInScreen - yInView));
		windowManager.updateViewLayout(this, mParams);
	}

	/**
	 * 更新view位置
	 * @param x
	 * @param y
	 */
	public void updateViewPosition(int x, int y) {
		mParams.x = x;
		mParams.y = y;
		windowManager.updateViewLayout(this, mParams);
	}

	/**
	 * 获得状态栏高度
	 * @return
	 */
	private int getStatusBarHeight() {
		if (isFullscreen) {
			return 0;
		}
		if (statusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = ((Integer) field.get(o)).intValue();
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusBarHeight;
	}

	/**
	 * 分发事件
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == 4) {
			Log.d("FloatMenuIcon", "BACK..");
		}
		return super.dispatchKeyEvent(event);
	}

	public int getViewWidth() {
		return viewWidth;
	}

	public int getViewHeight() {
		return viewHeight;
	}
	
	/**
	 * 悬浮窗清理操作
	 */
	public void clear() {
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
	}
	
	public String getTag() {
		return TAG;
	}
}
