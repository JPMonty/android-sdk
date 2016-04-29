package com.reign.ast.sdk;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * base list activity
 * @author zhouwenjia
 *
 */
public class BaseListActivity extends ListActivity {
	
	/** 返回按钮 */
	protected View backButton;

	/** back onClick */
	public OnClickListener backImgOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			BaseListActivity.this.finish();
		}
	};

	/**
	 * onCreate
	 */
	protected void onCreate(Bundle savedInstanceState) {
		if (BaseActivity.orientationType == 1) {
			setRequestedOrientation(1);
		}
		if (BaseActivity.orientationType == 2) {
			setRequestedOrientation(0);
		}
		super.onCreate(savedInstanceState);
	}

	public static int getResIdByName(Context context, String resourcesName) {
		return BaseActivity.getResIdByName(context, resourcesName);
	}

	public static int getDrawbleIdByName(Context context, String resourcesName) {
		return BaseActivity.getDrawbleIdByName(context, resourcesName);
	}

	public static int getStringIdByName(Context context, String resourcesName) {
		return BaseActivity.getStringIdByName(context, resourcesName);
	}

	public static int getResIdByName(Context context, String packageName, String resourcesName) {
		return BaseActivity.getResIdByName(context, packageName, resourcesName);
	}

	public void setContentViewByName(String resourcesName) {
		setContentView(getResIdByName(this, "layout", resourcesName));
	}

	public static Dialog createLoadingDiaLog(Context context, String msg) {
		return BaseActivity.createLoadingDiaLog(context, msg);
	}

	public static void dismissDialog(Dialog dlg) {
		BaseActivity.dismissDialog(dlg);
	}
}
