package com.reign.ast.sdk.alipay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络管理器
 * @author zhouwenjia
 * 
 */
public class NetworkManager {
	static final String TAG = "NetworkManager";
	private int connectTimeout = 30 * 1000;
	private int readTimeout = 30 * 1000;
	Proxy mProxy = null;
	Context mContext;

	/**
	 * 构造函数
	 * @param context
	 */
	public NetworkManager(Context context) {
		this.mContext = context;
		setDefaultHostnameVerifier();
	}

	/**
	 * 检测代理
	 */
	@SuppressWarnings("deprecation")
	private void detectProxy() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService (Context.CONNECTIVITY_SERVICE);  
        NetworkInfo ni = cm.getActiveNetworkInfo ();  
        if (ni != null && ni.isAvailable () && ni.getType () == ConnectivityManager.TYPE_MOBILE) {  
            String proxyHost = android.net.Proxy.getDefaultHost ();  
            int port = android.net.Proxy.getDefaultPort ();  
            if (null != proxyHost) {  
                final InetSocketAddress sa = new InetSocketAddress (proxyHost, port);  
                mProxy = new Proxy (Proxy.Type.HTTP, sa);  
            }  
        }  
	}

	/**
	 * 设置默认的hostName 验证
	 */
	private void setDefaultHostnameVerifier() {
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	/**
	 * 发送并等待返回
	 * @param strReqData
	 * @param strUrl
	 * @return
	 */
	public String SendAndWaitResponse(String strReqData, String strUrl) {
		detectProxy();

		String strResponse = null;
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("requestData", strReqData));

		HttpURLConnection httpConnect = null;
		try {
			UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(pairs, "utf-8");
			URL url = new URL(strUrl);

			if (null != this.mProxy) {
				httpConnect = (HttpURLConnection) url.openConnection(this.mProxy);
			} else {
				httpConnect = (HttpURLConnection) url.openConnection();
			}
			httpConnect.setConnectTimeout(this.connectTimeout);
			httpConnect.setReadTimeout(this.readTimeout);
			httpConnect.setDoOutput(true);
			httpConnect.addRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

			httpConnect.connect();

			OutputStream os = httpConnect.getOutputStream();
			p_entity.writeTo(os);
			os.flush();

			InputStream content = httpConnect.getInputStream();
			strResponse = BaseHelper.convertStreamToString(content);
			BaseHelper.log(TAG, "response " + strResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpConnect.disconnect();
		}
		return strResponse;
	}

	/**
	 * url 下载到文件
	 * @param context
	 * @param strurl
	 * @param path
	 * @return
	 */
	public boolean urlDownloadToFile(Context context, String strurl, String path) {
		boolean bRet = false;

		detectProxy();
		try {
			URL url = new URL(strurl);
			HttpURLConnection conn = null;
			if (this.mProxy != null) {
				conn = (HttpURLConnection) url.openConnection(this.mProxy);
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setConnectTimeout(this.connectTimeout);
			conn.setReadTimeout(this.readTimeout);
			conn.setDoInput(true);

			conn.connect();
			InputStream is = conn.getInputStream();

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
}
