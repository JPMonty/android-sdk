package com.reign.ast.sdk.util;

import android.util.Log;

/**
 * logger
 * @author zhouwenjia
 * 
 */
public class Logger {
	
	public static final int ALL = 1;
	public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	public static final int ASSERT = 7;
	public static boolean isLog = true;

	private static int filter = 3;

	public static void setFilter(int level) {
		filter = level;
	}

	public static boolean isLoggable(String tag, int level) {
		if (level < 4) {
			return false;
		}
		return true;
	}

	public static int i(String tag, String msg) {
		return (isLog) && (filter <= 4) ? Log.i(tag, msg) : 0;
	}

	public static int i(String tag, String msg, Throwable tr) {
		return (isLog) && (filter <= 4) ? Log.i(tag, msg, tr) : 0;
	}

	public static int e(String tag, String msg) {
		return (isLog) && (filter <= 6) ? Log.e(tag, msg) : 0;
	}

	public static int e(String tag, String msg, Throwable tr) {
		return (isLog) && (filter <= 6) ? Log.e(tag, msg, tr) : 0;
	}

	public static int d(String tag, String msg) {
		return (isLog) && (filter <= 3) ? Log.d(tag, msg) : 0;
	}

	public static int d(String tag, String msg, Throwable tr) {
		return (isLog) && (filter <= 3) ? Log.d(tag, msg, tr) : 0;
	}

	public static int w(String tag, String msg) {
		return (isLog) && (filter <= 5) ? Log.w(tag, msg) : 0;
	}

	public static int w(String tag, String msg, Throwable tr) {
		return (isLog) && (filter <= 5) ? Log.w(tag, msg, tr) : 0;
	}

	public static int v(String tag, String msg) {
		return (isLog) && (filter <= 2) ? Log.v(tag, msg) : 0;
	}

	public static int v(String tag, String msg, Throwable tr) {
		return (isLog) && (filter <= 2) ? Log.v(tag, msg, tr) : 0;
	}

	public static void getTraces(String tag) {
		Exception e = new Exception(tag);
		w(tag, tag, e);
	}

	/**
	 * 打印堆栈
	 * @param str
	 */
	public static void printStackTrace(String str) {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		for (int i = 0; i < st.length; i++) {
			d(str, i + ":" + st[i]);
		}
	}

	/**
	 * 打印堆栈
	 * @param str
	 * @param index
	 */
	public static void printStackTrace(String str, int index) {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		if (index < st.length) {
			for (int i = index; i < st.length; i++) {
				d(str, i + ":" + st[i]);
			}
		} else {
			d(str, "index invalid");
		}
	}

	public static void printStackTrace(String str, int begin, int end) {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		if (begin < st.length) {
			end++;
			end = end < st.length ? end : st.length;
			for (int i = begin; i < end; i++) {
				d(str, i + ":" + st[i]);
			}
		} else {
			d(str, "index invalid");
		}
	}
}
