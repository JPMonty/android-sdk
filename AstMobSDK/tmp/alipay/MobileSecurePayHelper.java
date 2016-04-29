package com.reign.ast.sdk.alipay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.reign.ast.sdk.manager.Constants;

/**
 * 移动安全支付helper
 * @author zhouwenjia
 * 
 */
public class MobileSecurePayHelper {
	static final String TAG = "MobileSecurePayHelper";
	private ProgressDialog mProgress = null;
	private Context mContext = null;

	/** 消息处理器 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 2:
					MobileSecurePayHelper.this.closeProgress();
					String cachePath = (String) msg.obj;
					MobileSecurePayHelper.this.showInstallConfirmDialog(MobileSecurePayHelper.this.mContext, cachePath);
				}
				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 构造函数
	 * @param context
	 */
	public MobileSecurePayHelper(Context context) {
		this.mContext = context;
	}

	/**
	 * 检测支付宝应用是否存在
	 * @return
	 */
	public boolean checkAlipayAppExist() {
		boolean isMobile_spExist = isAlipayAppExist();
		if (!isMobile_spExist) {
			// 获取系统缓冲绝对路径获取/data/data//cache目录
			File cacheDir = this.mContext.getCacheDir();
			final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";

			retrieveApkFromAssets(this.mContext, Constants.ALIPAY_PLUGIN_NAME, cachePath);

			this.mProgress = BaseHelper.showProgress(this.mContext, null, "正在检测安全支付服务版本", false, true);
			// 实例新线程检测是否有新版本进行下载
			new Thread(new Runnable() {
				public void run() {
					// 检测是否有新的版本
					PackageInfo apkInfo = MobileSecurePayHelper.getApkInfo(MobileSecurePayHelper.this.mContext, cachePath);
					String newApkdlUrl = checkNewUpdate(apkInfo);
					closeProgress();

					if (null != newApkdlUrl) {
						MobileSecurePayHelper.this.retrieveApkFromNet(MobileSecurePayHelper.this.mContext, newApkdlUrl, cachePath);
					}

					Message msg = new Message();
					msg.what = 2;
					msg.obj = cachePath;
					MobileSecurePayHelper.this.mHandler.sendMessage(msg);
				}
			}).start();
		}

		return isMobile_spExist;
	}

	/**
	 * 显示安装确认
	 * @param context
	 * @param cachePath
	 */
	public void showInstallConfirmDialog(final Context context, final String cachePath) {
		AlertDialog.Builder tDialog = new AlertDialog.Builder(context);

		tDialog.setIcon(getResIdByName(context, "drawable", "ast_mob_sdk_dalog_info"));
		tDialog.setTitle(context.getResources().getString(getResIdByName(context, "string", "ast_mob_sdk_pay_confirm_install_hint")));
		tDialog.setMessage(context.getResources().getString(getResIdByName(context, "string", "ast_mob_sdk_pay_confirm_install")));

		tDialog.setPositiveButton(getResIdByName(context, "string", "ast_mob_sdk_pay_ensure"),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 修改apk权限
						BaseHelper.chmod("777", cachePath);
						// 安装安全支付服务APK 
						Intent intent = new Intent("android.intent.action.VIEW");
						intent.addFlags(268435456);
						intent.setDataAndType(Uri.parse("file://" + cachePath), "application/vnd.android.package-archive");
						context.startActivity(intent);
					}
				});
		tDialog.setNegativeButton(
				context.getResources().getString(getResIdByName(context, "string", "ast_mob_sdk_pay_cancel")),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		tDialog.show();
	}

	/**
	 * 判断aplipay app是否存在
	 * @return
	 */
	public boolean isAlipayAppExist() {
		// 换了支付调用方式, 下面代码不执行
		return true;
//		PackageManager manager = this.mContext.getPackageManager();
//		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
//		for (int i = 0; i < pkgList.size(); i++) {
//			PackageInfo pI = (PackageInfo) pkgList.get(i);
//			if (pI.packageName.equalsIgnoreCase("com.alipay.android.app")) {
//				return true;
//			}
//		}
//		return false;
	}

	/**
	 * 从assets中检索apk
	 * @param context
	 * @param fileName
	 * @param path
	 * @return
	 */
	public boolean retrieveApkFromAssets(Context context, String fileName, String path) {
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
	 * 获得apk info
	 * @param context
	 * @param archiveFilePath
	 * @return
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_META_DATA);
		return apkInfo;
	}

	/**
	 * check new update
	 * @param packageInfo
	 * @return
	 */
	public String checkNewUpdate(PackageInfo packageInfo) {
		String url = null;
		try {
			JSONObject resp = sendCheckNewUpdate(packageInfo.versionName);
//			JSONObject resp = sendCheckNewUpdate("1.0.0");
			if (resp.getString("needUpdate").equalsIgnoreCase("true")) {
				url = resp.getString("updateUrl");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return url;
	}

	/**
	 * send check new update
	 * @param versionName
	 * @return
	 */
	public JSONObject sendCheckNewUpdate(String versionName) {
		JSONObject objResp = null;
		try {
			JSONObject req = new JSONObject ();  
            req.put (AlixDefine.action, AlixDefine.actionUpdate);  
            JSONObject data = new JSONObject ();  
            data.put (AlixDefine.platform, "android");  
            data.put (AlixDefine.VERSION, versionName);  
            data.put (AlixDefine.partner, "");  
            req.put (AlixDefine.data, data);  
            objResp = sendRequest (req.toString ());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return objResp;
	}

	/**
	 * 发送请求
	 * @param content
	 * @return
	 */
	public JSONObject sendRequest(String content) {
		NetworkManager nM = new NetworkManager(this.mContext);

		JSONObject jsonResponse = null;
		try {
			String response = null;

			synchronized (nM) {
				response = nM.SendAndWaitResponse(content, Constants.server_url);
			}

			jsonResponse = new JSONObject(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != jsonResponse) {
			BaseHelper.log(TAG, jsonResponse.toString());
		}
		return jsonResponse;
	}

	/**
	 * 根据name获得资源id
	 * @param context
	 * @param packageName
	 * @param resourcesName
	 * @return
	 */
	public static int getResIdByName(Context context, String packageName, String resourcesName) {
		Resources resources = context.getResources();
		int id = resources.getIdentifier(resourcesName, packageName, context.getPackageName());
		if (id == 0) {
			Log.e("MobileSecurePayHelper", "读取资源文件失败");
		}
		return id;
	}

	/**
	 * 从net检索apk
	 * @param context
	 * @param strurl
	 * @param filename
	 * @return
	 */
	public boolean retrieveApkFromNet(Context context, String strurl, String filename) {
		boolean bRet = false;
		try {
			NetworkManager nM = new NetworkManager(this.mContext);
			bRet = nM.urlDownloadToFile(context, strurl, filename);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * 关闭progress
	 */
	void closeProgress() {
		try {
			if (null != this.mProgress) {
				this.mProgress.dismiss();
				this.mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
