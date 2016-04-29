package com.reign.ast.sdk.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 显示util
 * @author zhouwenjia
 * 
 */
public class DisplayUtil {
	public static float getScale(Context ctx) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = ctx.getResources().getDisplayMetrics();

		return dm.density;
	}

	public static int px2dip(float pxValue, float scale) {
		return (int) (pxValue / scale + 0.5F);
	}

	public static int dip2px(float dipValue, float scale) {
		return (int) (dipValue * scale + 0.5F);
	}

	public static int px2sp(float pxValue, float fontScale) {
		return (int) (pxValue / fontScale + 0.5F);
	}

	public static int sp2px(float spValue, float fontScale) {
		return (int) (spValue * fontScale + 0.5F);
	}
}
