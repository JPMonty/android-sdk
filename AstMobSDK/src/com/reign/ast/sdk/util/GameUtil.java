package com.reign.ast.sdk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.reign.ast.sdk.manager.AstGamePlatform;
import com.reign.ast.sdk.pojo.GameAccount;

/**
 * 游戏util
 * 
 * @author zhouwenjia
 * 
 */
public class GameUtil {
	private static final String TAG = "GameUtil";
	/** 渠道id */
	private static String source;
	/** 子渠道id */
	private static String subSource;

	/**
	 * 增加后缀
	 * 
	 * @param userName
	 * @return
	 */
	public static String addSuffix(String userName) {
		return userName;
	}

	/**
	 * 删除后缀
	 * 
	 * @param userName
	 * @return
	 */
	public static String deleteAstSuffix(String userName) {
		return userName;
	}

	/**
	 * 获得第三方userName
	 * 
	 * @param userName
	 * @param provider
	 * @return
	 */
	public static String getThridUserName(String userName, String provider) {
		return userName + provider;
	}

	/**
	 * 获得android广告激活签名
	 * 
	 * @param mac
	 * @param idfa
	 * @param openudid
	 * @param gameId
	 * @return
	 */
	public static String getAndFirstSig(String mac, String idfa,
			String openudid, String gameId) {
		StringBuilder sb = new StringBuilder();
		sb.append(mac);
		sb.append(idfa);
		sb.append(openudid);
		sb.append(gameId);
		String activeKey = AstGamePlatform.getInstance().getAppInfo().activeKey;
		sb.append(activeKey);

		String enStr = Md5Util.crypt(sb.toString());
		return enStr;
	}

	public static String getPhoneRegSig(String mac, String idfa,
			String opendid, String mobile, String password, String gameId,
			long ts) {

		StringBuilder sb = new StringBuilder();
		sb.append(mac);
		sb.append(idfa);
		sb.append(opendid);
		sb.append(mobile);
		sb.append(password).append(gameId).append(ts);

		String regKey = AstGamePlatform.getInstance().getAppInfo().regKey;
		sb.append(regKey);

		String enStr = Md5Util.crypt(sb.toString());
		return enStr;
	}

	/**
	 * 获得快速注册sig
	 * 
	 * @param ts
	 * @return
	 */
	public static String getQuickRegSig(String mac, String idfa,
			String openudid, String gameId, long ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(mac);
		sb.append(idfa);
		sb.append(openudid);
		sb.append(gameId);
		sb.append(ts);
		String regKey = AstGamePlatform.getInstance().getAppInfo().regKey;
		sb.append(regKey);

		String enStr = Md5Util.crypt(sb.toString());
		return enStr;
	}

	public static String getValidateRegistCode(String code, String mobile,
			String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(code).append(mobile).append(ts);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		String enStr = Md5Util.crypt(sb.append(bindKey).toString());
		return enStr;

	}

	public static String getValidateGuestBindCode(String code, String mobile,
			String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(code).append(mobile).append(ts);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		String enStr = Md5Util.crypt(sb.append(bindKey).toString());
		return enStr;

	}

	public static String getValidateForgetPwdCode(String code, String mobile,
			String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(code).append(mobile).append(ts);
		String changePwdKey = AstGamePlatform.getInstance().getAppInfo().changePwdKey;
		String enStr = Md5Util.crypt(sb.append(changePwdKey).toString());
		return enStr;

	}

	public static String getSendPhoneRegisterSig(String mobile, String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(mobile).append(ts);
		String regKey = AstGamePlatform.getInstance().getAppInfo().regKey;
		String enStr = Md5Util.crypt(sb.append(regKey).toString());
		return enStr;

	}

	public static String getSendGuestBindSig(String mobile, String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(mobile).append(ts);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		String enStr = Md5Util.crypt(sb.append(bindKey).toString());
		return enStr;

	}

	public static String getGuestBindPhoneSig(String mobile, String ts) {
		StringBuffer sb = new StringBuffer();
		sb.append(mobile).append(ts);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		String enStr = Md5Util.crypt(sb.append(bindKey).toString());
		return enStr;

	}

	public static String getTokenLoginSig(String mac, String idfa,
			String openudid, String username, String gameId,
			String accessToken, String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(mac).append(idfa).append(openudid).append(username)
				.append(gameId).append(accessToken).append(ts);

		String loginKey = AstGamePlatform.getInstance().getAppInfo().loginKey;

		String enStr = Md5Util.crypt(sb.append(loginKey).toString());

		return enStr;

	}

	public static String getQuickLoginSig(String mac, String idfa,
			String openudid, String gameId, String accessToken, String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(mac).append(idfa).append(openudid).append(gameId)
				.append(accessToken).append(ts);

		String loginKey = AstGamePlatform.getInstance().getAppInfo().loginKey;

		String enStr = Md5Util.crypt(sb.append(loginKey).toString());

		return enStr;

	}

	/**
	 * 获得绑定临时账号sig
	 * 
	 * @param ts
	 * @param tmpPlayerId
	 * @param username
	 * @param password
	 * @return
	 */
	public static String getBindQuickUserSig(long ts, String tmpPlayerId,
			String username, String password) {
		StringBuilder sb = new StringBuilder();
		sb.append(ts);
		sb.append(tmpPlayerId);
		sb.append(username);
		sb.append(password);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		sb.append(bindKey);

		String enStr = Md5Util.crypt(sb.toString());
		return enStr;
	}

	/**
	 * 获得firstStart的签名
	 * 
	 * @param mac
	 * @param idfa
	 * @param openudid
	 * @param gameId
	 * @return
	 */
	public static String getFirstStartSig(String mac, String idfa,
			String openudid, String gameId) {
		StringBuilder sb = new StringBuilder();
		sb.append(mac);
		sb.append(idfa);
		sb.append(openudid);
		sb.append(gameId);
		String activeKey = AstGamePlatform.getInstance().getAppInfo().activeKey;
		sb.append(activeKey);

		String enStr = Md5Util.crypt(sb.toString());
		return enStr;
	}

	/**
	 * 获得创建订单sig
	 * 
	 * @param userId
	 * @param gameId
	 * @param gameName
	 * @param extraAppData
	 * @param serverId
	 * @param roleId
	 * @param money
	 * @param gold
	 * @param ts
	 * @param yx
	 * @param extra
	 * @return
	 */
	public static String getCreateOrderSig(String userId, String gameId,
			String gameName, String extraAppData, String serverId,
			String roleId, String money, String gold, long ts, String yx,
			String extra) {
		StringBuilder sb = new StringBuilder();
		sb.append(userId);
		sb.append(gameId);

		sb.append(gameName);
		sb.append(extraAppData);

		sb.append(serverId);
		sb.append(roleId);
		sb.append(money);
		sb.append(gold);
		sb.append(ts);
		sb.append(yx);
		sb.append(extra);

		String payKey = AstGamePlatform.getInstance().getAppInfo().payKey;
		sb.append(payKey);

		String enStr = Md5Util.crypt(sb.toString());
		return enStr;
	}

	/**
	 * 获得19pay 签名
	 * 
	 * @param cardType
	 * @param amount
	 * @param cardNum
	 * @param cardPassword
	 * @param selectAmount
	 * @param gold
	 * @param gameId
	 * @param serverId
	 * @param roleId
	 * @param userId
	 * @param ts
	 * @param yx
	 * @param extra
	 * @return
	 */
	public static String get19PaySig(String cardType, String amount,
			String cardNum, String cardPassword, String selectAmount,
			String gold, String gameId, String serverId, String roleId,
			String userId, long ts, String yx, String extra) {
		StringBuilder sb = new StringBuilder();
		sb.append(cardType);
		sb.append(amount);
		sb.append(cardNum);
		sb.append(cardPassword);
		sb.append(selectAmount);
		sb.append(gold);
		sb.append(gameId);
		sb.append(serverId);
		sb.append(roleId);
		sb.append(userId);
		sb.append(ts);
		sb.append(yx);
		sb.append(extra);

		String payKey = AstGamePlatform.getInstance().getAppInfo().payKey;
		sb.append(payKey);

		String enStr = Md5Util.crypt(sb.toString());
		return enStr;
	}

	/**
	 * 获得设备信息
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getDeviceInfo(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService("phone");
		StringBuilder sb = new StringBuilder();
		sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
		sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
		sb.append("\nLine1Number = " + tm.getLine1Number());
		sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		sb.append("\nNetworkType = " + tm.getNetworkType());
		sb.append("\nPhoneType = " + tm.getPhoneType());
		sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		sb.append("\nSimOperator = " + tm.getSimOperator());
		sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
		sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
		sb.append("\nSimState = " + tm.getSimState());
		sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
		Log.e("info", sb.toString());
		return sb.toString();
	}

	/**
	 * 获得设备号
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getDeviceId(Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService("phone");
		return tm.getDeviceId() == null ? "" : tm.getDeviceId();
	}

	/**
	 * 从assets中检索文件
	 * 
	 * @param context
	 * @param fileName
	 * @param path
	 * @return
	 */
	public static boolean retrieveFileFromAssets(Context context,
			String fileName, String path) {
		boolean bRet = false;
		try {
			InputStream is = context.getAssets().open(fileName);

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * 红色字体
	 * 
	 * @param str
	 * @return
	 */
	public static String redFontString(Object str) {
		return "<font color='#D11301'>" + str + "</font>";
	}

	/**
	 * 将浮点类型转换为string
	 * 
	 * @param value
	 * @return
	 */
	public static String converFloat(float value) {
		try {
			DecimalFormat df = new DecimalFormat("#.00");
			String va = df.format(value);
			float v = Float.parseFloat(va);
			if ((v % 2.0F == 0.0F) || (v % 2.0F == 1.0F)) {
				return va.substring(0, va.indexOf("."));
			}
			return String.valueOf(Float.parseFloat(va));
		} catch (Exception localException) {
		}
		return String.valueOf(value);
	}

	/**
	 * 高亮hot title
	 * 
	 * @param paramSpannable
	 * @param filter
	 */
	public static void highlightHotTitle(Spannable paramSpannable, String filter) {
		try {
			Pattern hotPattern = Pattern.compile(filter, 2);
			Matcher hotMatcher = hotPattern.matcher(paramSpannable);
			ForegroundColorSpan localForegroundColorSpan3 = new ForegroundColorSpan(
					Color.rgb(255, 0, 0));
			while (hotMatcher.find()) {
				int st = hotMatcher.start();
				int end = hotMatcher.end();
				paramSpannable.setSpan(localForegroundColorSpan3, st, end, 33);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断char是否是中文
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if ((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
				|| (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断str是否是中文
	 * 
	 * @param strName
	 * @return
	 */
	public static Boolean isChinese(String strName) {
		char[] ch = strName.toCharArray();
		boolean isChinese = false;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				isChinese = true;
				break;
			}
		}
		return Boolean.valueOf(isChinese);
	}

	/**
	 * 是否是大写
	 * 
	 * @param word
	 * @return
	 */
	public static boolean isUpperCase(String word) {
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (Character.isUpperCase(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 从asset中获得
	 * 
	 * @param ctx
	 * @param fileName
	 * @return
	 */
	public static String getFromAsset(Context ctx, String fileName) {
		String result = "";
		try {
			InputStream in = ctx.getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);

			result = EncodingUtils.getString(buffer, "utf-8");
			if (result != null) {
				result = result.trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 设置source
	 * 
	 * @param sourceId
	 */
	public static void setSource(String source) {
		GameUtil.source = source;
	}

	/**
	 * 设置subSource
	 * 
	 * @param subSource
	 */
	public static void setSubSource(String subSource) {
		GameUtil.subSource = subSource;
	}

	/**
	 * 获得source
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getSource(Context ctx) {
		if (null == source || source.equals("")) {
			parseSource(ctx);
		}
		return source;
	}

	/**
	 * 获得subSource
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getSubSource(Context ctx) {
		if (null == source || source.equals("")) {
			parseSource(ctx);
		}
		return subSource;
	}

	/**
	 * parse source和subSource
	 * 
	 * @param ctx
	 */
	public static void parseSource(Context ctx) {
		String content = getFromAsset(ctx, "ast_ad_conf");
		if (isBlankString(content)) {
			// 没有配置ast_ad_conf
			source = AstGamePlatform.getInstance().getAppInfo().channel;
			subSource = "";
		} else {
			String[] arr = content.split("#");
			source = arr[0];
			if (arr.length >= 2) {
				subSource = arr[1];
			} else {
				subSource = "";
			}
		}
	}

	/**
	 * 判断是否有配置广告source、subSource
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean hasConfAd(Context ctx) {
		String content = getFromAsset(ctx, "ast_ad_conf");
		if (isBlankString(content)) {
			// 认为没有配置ast_ad_conf
			return false;
		}
		return true;
	}

	/**
	 * 获得versionName
	 * 
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static String getVersionName(Context ctx) throws Exception {
		PackageManager packageManager = ctx.getPackageManager();

		PackageInfo packInfo = packageManager.getPackageInfo(
				ctx.getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}

	/**
	 * 获得version code
	 * 
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static int getVersionCode(Context ctx) throws Exception {
		PackageManager packageManager = ctx.getPackageManager();

		PackageInfo packInfo = packageManager.getPackageInfo(
				ctx.getPackageName(), 0);
		int version = packInfo.versionCode;
		return version;
	}

	/**
	 * 像素 设备独立像素
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5F);
	}

	/**
	 * 设备独立像素 像素
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5F);
	}

	/**
	 * 登陆类型mark
	 * 
	 * @param context
	 * @param loginType
	 */
	public static void loginTypeMark(Context context, int loginType) {
		SharedPreferences pref = context.getSharedPreferences(
				"login_type_sequence", 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("login_type_sequence", loginType);
		editor.commit();
	}

	/**
	 * 获得登陆类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getLoginType(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				"login_type_sequence", 0);
		int content = pref.getInt("login_type_sequence", -1);
		return content;
	}

	/**
	 * 保存账号信息
	 * 
	 * @param context
	 * @param account
	 */
	public static void saveAccountInfo(Context context, GameAccount account) {
		if ((context == null) || (account == null)) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(
				"login_account_info", 0);
		String content = pref.getString("login_account_info", "");
		content = getAccountInfo(account.type, account.name, account.pwd,
				account.token) + ";" + content;
		List<GameAccount> gas = new ArrayList<GameAccount>();
		boolean hasQuick = false;
		for (GameAccount ga : getGameAccounts(content, true)) {
			if (ga.getType() == GameAccount.QUICK && hasQuick) {
				hasQuick = true;
				continue;
			}
			if (!gas.contains(ga)) {
				gas.add(ga);
			}
		}

		StringBuilder strfilter = new StringBuilder();
		for (int i = 0; i < gas.size(); i++) {
			if (i >= 6) {
				// --<最多存储6个>--
				break;
			}
			strfilter.append(gas.get(i)).append(";");
		}

		SharedPreferences.Editor editor = pref.edit();
		Logger.d(TAG, "strfilter:" + strfilter);
		editor.putString("login_account_info", strfilter.toString());
		boolean is = editor.commit();
		Logger.d(TAG, "saveAccountInfo:" + is);
	}

	/**
	 * 获得账号信息
	 * 
	 * @param type
	 * @param userName
	 * @param password
	 * @return
	 */
	public static String getAccountInfo(int type, String userName,
			String password, String token) {
		if (password == null) {
			password = "";
		}
		if (userName == null) {
			userName = "";
		}
		return type + ":" + userName + ":" + password + ":" + token;
	}

	/**
	 * 获得user历史
	 * 
	 * @param context
	 * @return
	 */
	public static List<GameAccount> getUserHistory(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				"login_account_info", Context.MODE_PRIVATE);
		String content = pref.getString("login_account_info", "");
		return getGameAccounts(content, true);
	}

	/**
	 * 获得users
	 * 
	 * @param context
	 * @return
	 */
	public static List<GameAccount> getUsers(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				"login_account_info", 0);
		String content = pref.getString("login_account_info", "");
		return getGameAccounts(content, false);
	}

	/**
	 * 获得游戏账号
	 * 
	 * @param content
	 * @param isAllUser
	 * @return
	 */
	public static List<GameAccount> getGameAccounts(String content,
			boolean isAllUser) {
		List<GameAccount> gameAccount = new ArrayList<GameAccount>();
		if (!"".equals(content)) {
			String[] cs = content.split(";");
			for (String c : cs) {
				GameAccount account = new GameAccount();
				String[] ats = c.split(":");
				if (ats.length == 4) {
					account.type = Integer.parseInt(ats[0]);
					account.name = ats[1];
					account.pwd = ats[2];
					account.token = ats[3];
				}
				if ((isAllUser) || (account.type != 0)) {
					gameAccount.add(account);
				}
			}
		}
		return gameAccount;
	}

	/**
	 * 获得账号的userName
	 * 
	 * @param account
	 * @return
	 */
	public static String getUserName(GameAccount account) {
		String[] res = account.toString().split(":");
		return res[1];
	}

	/**
	 * 获得userInfo的userName
	 * 
	 * @param userInfo
	 * @return
	 */
	public static String getUserName(String userInfo) {
		String[] res = userInfo.split(":");
		return res[1];
	}

	/**
	 * 获得native phone number
	 * 
	 * @param context
	 * @return
	 */
	public static String getNativePhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService("phone");
		return telephonyManager.getLine1Number();
	}

	/**
	 * 获得provider名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getProvidersName(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService("phone");
		String ProvidersName = null;

		String IMSI = telephonyManager.getSubscriberId();

		System.out.println(IMSI);
		if ((IMSI.startsWith("46000")) || (IMSI.startsWith("46002")))
			ProvidersName = "中国移动";
		else if (IMSI.startsWith("46001"))
			ProvidersName = "中国联通";
		else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		}
		return ProvidersName;
	}

	/**
	 * 拷贝
	 * 
	 * @param aSourceFile
	 * @param aTargetFile
	 * @param aAppend
	 */
	public static void copyWithChannels(File aSourceFile, File aTargetFile,
			boolean aAppend) {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			try {
				inStream = new FileInputStream(aSourceFile);
				inChannel = inStream.getChannel();
				outStream = new FileOutputStream(aTargetFile, aAppend);
				outChannel = outStream.getChannel();
				long bytesTransferred = 0L;

				while (bytesTransferred < inChannel.size()) {
					bytesTransferred += inChannel.transferTo(0L,
							inChannel.size(), outChannel);
				}
			} finally {
				if (inChannel != null) {
					inChannel.close();
				}
				if (outChannel != null) {
					outChannel.close();
				}
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			Logger.e(TAG, "copyWithChannels FileNotFoundException");
		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.e(TAG, "copyWithChannels IOException");
		}
	}

	/**
	 * 获得apk路径
	 * 
	 * @param context
	 * @return
	 */
	public static final String getApkPath(Context context) {
		try {
			String source = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), 0).sourceDir;
			Logger.d(TAG, "getApkPath:" + source);
			return source;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得source目录的md5
	 * 
	 * @param context
	 * @return
	 */
	public static String getGameMd5(Context context) {
		try {
			String apkPath = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), 0).sourceDir;
			return Md5Util.checkSum(apkPath);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断是否是空串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlankString(String str) {
		return (null == str || str.equals(""));
	}

	public String getTag() {
		return TAG;
	}

	/**
	 * @param mac
	 * @param idfa
	 * @param openUdid
	 * @param gameId
	 * @param userName
	 * @param password
	 * @param ts
	 * @return
	 */
	public static String getRegTicket(String mac, String idfa, String openUdid,
			String gameId, String userName, String password, String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(mac).append(idfa).append(openUdid).append(userName)
				.append(password).append(gameId).append(ts);
		String regKey = AstGamePlatform.getInstance().getAppInfo().regKey;
		String enStr = Md5Util.crypt(sb.append(regKey).toString());
		return enStr;
	}

	public static String getSendRegetPasswdCaptchaSig(String mobile, String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(mobile).append(ts);
		String changePwdKey = AstGamePlatform.getInstance().getAppInfo().changePwdKey;
		String enStr = Md5Util.crypt(sb.append(changePwdKey).toString());
		return enStr;
	}

	public static String getResetPasswordSig(String mobile, String password,
			String gameId, String key, String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(mobile).append(password).append(gameId).append(key)
				.append(ts);
		String changePwdKey = AstGamePlatform.getInstance().getAppInfo().changePwdKey;
		String enStr = Md5Util.crypt(sb.append(changePwdKey).toString());
		return enStr;
	}

	public static String getGuestBindCode(String tmpPlayerId, String password,
			String mobile, String gameId, String ts) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(tmpPlayerId).append(password).append(gameId).append(mobile)
				.append(ts);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		String enStr = Md5Util.crypt(sb.append(bindKey).toString());
		return enStr;
	}

	public static void removeGuest(Context context) {
		if ((context == null)) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(
				"login_account_info", 0);
		String content = pref.getString("login_account_info", "");
		List<GameAccount> gas = new ArrayList<GameAccount>();

		for (GameAccount ga : getGameAccounts(content, true)) {
			if (!gas.contains(ga) && ga.getType() != GameAccount.QUICK) {
				gas.add(ga);
			}
		}

		StringBuilder strfilter = new StringBuilder();
		for (int i = 0; i < gas.size(); i++) {
			if (i >= 6) {
				// --<最多存储6个>--
				break;
			}
			strfilter.append(gas.get(i)).append(";");
		}

		SharedPreferences.Editor editor = pref.edit();
		Logger.d(TAG, "strfilter:" + strfilter);
		editor.putString("login_account_info", strfilter.toString());
		boolean is = editor.commit();
		Logger.d(TAG, "removeGuest:" + is);
	}
	
	
	public static void removeByUsername(Context context, String username) {
		if ((context == null)) {
			return;
		}
		SharedPreferences pref = context.getSharedPreferences(
				"login_account_info", 0);
		String content = pref.getString("login_account_info", "");
		List<GameAccount> gas = new ArrayList<GameAccount>();

		for (GameAccount ga : getGameAccounts(content, true)) {
			if (!gas.contains(ga) && !username.equals(ga.name)) {
				gas.add(ga);
			}
		}

		StringBuilder strfilter = new StringBuilder();
		for (int i = 0; i < gas.size(); i++) {
			if (i >= 6) {
				// --<最多存储6个>--
				break;
			}
			strfilter.append(gas.get(i)).append(";");
		}

		SharedPreferences.Editor editor = pref.edit();
		Logger.d(TAG, "strfilter:" + strfilter);
		editor.putString("login_account_info", strfilter.toString());
		boolean is = editor.commit();
		Logger.d(TAG, "removeGuest:" + is);
	}
	
	

	public static String getLoginBindSig(String string, String string2,
			String string3, String string4, String string5, String string6,
			String string7, String string8, String string9) {
		StringBuilder sb = new StringBuilder();
		sb.append(string).append(string2).append(string3).append(string4)
				.append(string5).append(string6).append(string7)
				.append(string8).append(string9);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		String enStr = Md5Util.crypt(sb.append(bindKey).toString());
		return enStr;
	}

	public static String getIsMobileBindSig(String username, String ts) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(username).append(ts);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		String enStr = Md5Util.crypt(sb.append(bindKey).toString());
		return enStr;
	}

	public static String getAstBindCode(String username, String mobile,
			String ts) {
		StringBuilder sb = new StringBuilder();
		sb.append(username).append(mobile).append(ts);
		String bindKey = AstGamePlatform.getInstance().getAppInfo().bindKey;
		String enStr = Md5Util.crypt(sb.append(bindKey).toString());
		return enStr;
	}

}
