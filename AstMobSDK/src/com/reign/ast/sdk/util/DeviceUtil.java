package com.reign.ast.sdk.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 设备util
 * @author zhouwenjia
 *
 */
public class DeviceUtil {

	public static final String TAG = "OpenUDID";
	public static final String PREF_KEY = "openudid";
	public static final String PREFS_NAME = "openudid_prefs";
	public static String openUdid = null;

	/**
	 * debuglog
	 * @param lmsg
	 */
	private static void debugLog(String lmsg) {
		Log.d(PREF_KEY, lmsg);
		return;
	}
	
	/**
	 * 同步context
	 * @param mContext
	 */
	public static void syncContext(Context mContext) {
		if (openUdid == null) {
			Context openContext = null;
			try {
				openContext = mContext.createPackageContext("net.openudid.android", 2);
				mContext = openContext;
			} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
				// TODO
			}
			SharedPreferences mPreferences = mContext.getSharedPreferences(PREFS_NAME, 1);
			String _keyInPref = mPreferences.getString(PREF_KEY, null);
			if (_keyInPref == null) {
				generateOpenUDIDInContext(mContext);
				SharedPreferences.Editor e = mPreferences.edit();
				e.putString(PREF_KEY, openUdid);
				e.commit();
			} else {
				openUdid = _keyInPref;
			}
		}
	}
	
	/**
	 * 获得设备版本号
	 * @return
	 */
	public static String getDeviceVersion() {
		String version = android.os.Build.VERSION.RELEASE;
		return version;
	}
	
	/**
	 * 获得设备型号
	 * @return
	 */
	public static String getDeviceType() {
		String device = android.os.Build.MODEL;
		return device;
	}

	/**
	 * 从context中获得OpenUDID
	 * @return
	 */
	public static String getOpenUDIDInContext() {
		return openUdid;
	}

	/**
	 * 获得公司UDID
	 * @param corpIdentifier
	 * @return
	 */
	public static String getCorpUDID(String corpIdentifier) {
		return Md5(String.format("%s.%s", new Object[] { corpIdentifier, getOpenUDIDInContext() }));
	}

	/**
	 * 从context中生成OpenUDID
	 * @param mContext
	 */
	private static void generateOpenUDIDInContext(Context mContext) {
		// 先取mac
		generateMACId(mContext);
		if (null != openUdid) {
			return;
		}

		// 再取imei
		generateIMEIId(mContext);

		if (null != openUdid) {
			return;
		}

		// 没有的话随机
		generateRandomNumber();

		debugLog(openUdid);
	}

	/**
	 * 生成imei id
	 * @param mContext
	 */
	private static void generateIMEIId(Context mContext) {
		try {
			TelephonyManager TelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			String szImei = TelephonyMgr.getDeviceId();

			if ((null != szImei) && (!szImei.substring(0, 3).equals("000"))) {
				openUdid = "IMEI:" + szImei;
			}
		} catch (Exception localException) {
			// ignore
		}
	}

	/**
	 * 生成蓝牙id
	 */
	public static void generateBlueToothId() {
		try {
			BluetoothAdapter m_BluetoothAdapter = null;
			m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			String m_szBTMAC = m_BluetoothAdapter.getAddress();
			if (null != m_szBTMAC) {
				openUdid = "BT:" + m_szBTMAC;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成mac id
	 * @param mContext
	 */
	private static void generateMACId(Context mContext) {
		try {
			WifiManager wifiMan = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMan.getConnectionInfo();

			String macAddr = wifiInfo.getMacAddress();
			if (null != macAddr) {
				openUdid = "MAC:" + macAddr;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * md5
	 * @param input
	 * @return
	 */
	private static String Md5(String input) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(input.getBytes(), 0, input.length());
		byte[] p_md5Data = m.digest();

		String mOutput = new String();
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = 0xFF & p_md5Data[i];

			if (b <= 15) {
				mOutput = mOutput + "0";
			}

			mOutput = mOutput + Integer.toHexString(b);
		}

		return mOutput.toUpperCase();
	}

	/**
	 * 生成随机数字
	 */
	private static void generateRandomNumber() {
		openUdid = Md5(UUID.randomUUID().toString());
	}

	/**
	 * 生成系统id
	 */
	public static void generateSystemId() {
		String fp = String.format("%s/%s/%s/%s:%s/%s/%s:%s/%s/%d-%s-%s-%s-%s",
				new Object[] { Build.BRAND, Build.PRODUCT, Build.DEVICE,
						Build.BOARD, Build.VERSION.RELEASE, Build.ID,
						Build.VERSION.INCREMENTAL, Build.TYPE, Build.TAGS,
						Long.valueOf(Build.TIME), Build.DISPLAY, Build.HOST,
						Build.MANUFACTURER, Build.MODEL });

		debugLog(fp);
		if (null != fp) {
			openUdid = Md5(fp);
		}
	}

}
