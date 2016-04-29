package com.reign.ast.sdk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * base activity
 * @author zhouwenjia
 *
 */
public class BaseActivity extends FragmentActivity {
	
	/** 返回按钮 */
	protected View backButton;
	static String TAG = "BaseActivity";
	
	/** 默认横屏 */
	public static int orientationType = 2;

	/** back onClick */
	public OnClickListener backImgOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			BaseActivity.this.finish();
		}
	};

	/**
	 * onCreate
	 */
	protected void onCreate(Bundle savedInstanceState) {
		if (orientationType == 1) {
			setRequestedOrientation(1);
		}
		if (orientationType == 2) {
			setRequestedOrientation(0);
		}
		super.onCreate(savedInstanceState);
	}

	public static boolean isLandscape() {
		if (orientationType == 2) {
			return true;
		}
		return false;
	}

	/**
	 * 通过name获得resId
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int getResIdByName(Context context, String resourcesName) {
		Resources resources = context.getResources();
		int id = resources.getIdentifier(resourcesName, "id", context.getPackageName());
		if (0 == id) {
			Log.e(TAG, "读取资源文件失败, resourcesName: " + resourcesName);
			throw new RuntimeException("读取资源文件失败, resourcesName: " + resourcesName);
		}
		return id;
	}

	/**
	 * 通过name获得Drawble
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int getDrawbleIdByName(Context context, String resourcesName) {
		Resources resources = context.getResources();
		int id = resources.getIdentifier(resourcesName, "drawable", context.getPackageName());
		if (0 == id) {
			Log.e(TAG, "读取资源文件失败, resourcesName: " + resourcesName);
			throw new RuntimeException("读取资源文件失败, resourcesName: " + resourcesName);
		}
		return id;
	}

	/**
	 * 通过name获得stringId
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int getStringIdByName(Context context, String resourcesName) {
		Resources resources = context.getResources();
		int id = resources.getIdentifier(resourcesName, "string", context.getPackageName());
		if (0 == id) {
			Log.e(TAG, "读取资源文件失败, resourcesName: " + resourcesName);
			throw new RuntimeException("读取资源文件失败, resourcesName: " + resourcesName);
		}
		return id;
	}

	/**
	 * 通过name获得colorId
	 * @param context
	 * @param resourcesName
	 * @return
	 */
	public static int getColorIdByName(Context context, String resourcesName) {
		Resources resources = context.getResources();
		int id = resources.getIdentifier(resourcesName, "color", context.getPackageName());
		if (0 == id) {
			Log.e(TAG, "读取资源文件失败, resourcesName: " + resourcesName);
			throw new RuntimeException("读取资源文件失败, resourcesName: " + resourcesName);
		}
		return id;
	}

	/**
	 * 通过name获得resId
	 * @param context
	 * @param reourcesType
	 * @param resourcesName
	 * @return
	 */
	public static int getResIdByName(Context context, String reourcesType, String resourcesName) {
		Resources resources = context.getResources();
		int id = resources.getIdentifier(resourcesName, reourcesType, context.getPackageName());
		if (0 == id) {
			Log.e(TAG, "读取资源文件失败, resourcesName: " + resourcesName);
			throw new RuntimeException("读取资源文件失败, resourcesName: " + resourcesName);
		}
		return id;
	}

	public void setContentViewByName(String resourcesName) {
		setContentView(getResIdByName(this, "layout", resourcesName));
	}

	public static int getLayoutIdByName(Context context, String resourcesName) {
		return getResIdByName(context, "layout", resourcesName);
	}

	public static int getAnimIdByName(Context context, String resourcesName) {
		return getResIdByName(context, "anim", resourcesName);
	}

	public static int getStyleIdByName(Context context, String resourcesName) {
		return getResIdByName(context, "style", resourcesName);
	}

	public static Dialog createLoadingDiaLog(Context context, String msg) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setMessage(msg);
		dialog.setCancelable(false);
		return dialog;
	}

	public String getStringByRes(String resName) {
		return getString(getStringIdByName(this, resName));
	}

	public static void dismissDialog(Dialog dlg) {
		if (null != dlg) {
			dlg.dismiss();
		}
	}
}
