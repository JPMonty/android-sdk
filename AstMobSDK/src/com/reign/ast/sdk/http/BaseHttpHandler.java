package com.reign.ast.sdk.http;

import java.io.InputStream;
import java.net.UnknownHostException;

import com.reign.ast.sdk.http.async.AsyncHttpClient;
import com.reign.ast.sdk.http.async.AsyncHttpResponseHandler;
import com.reign.ast.sdk.http.async.RequestParams;
import com.reign.ast.sdk.manager.ErrorCodeTransfer;
import com.reign.ast.sdk.util.Logger;

/**
 * 基本http交易
 * @author zhouwenjia
 *
 */
public abstract class BaseHttpHandler {
	private String TAG;
	protected HttpCallback callback;
	protected String url;
	protected RequestParams params;
	protected AsyncHttpClient asyncHttpClient;
	
	/** 异步http response handler */
	private AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
		public void onStart() {
		}

		public void onFinish() {
		}

		/**
		 * 成功处理
		 */
		public void onSuccess(String content) {
			ResponseEntity entity = BaseHttpHandler.this.parseData(content);
			if (null != BaseHttpHandler.this.callback)
				if (1 == entity.getCode()) {
					// code = 1认为成功
					BaseHttpHandler.this.callback.onSuccess(entity.getCode(), entity.getMsg(), entity.getData());
				} else {
					BaseHttpHandler.this.callback.onFailure(entity.getCode(), entity.getMsg(), entity.getData());
				}
		}

		/**
		 * 失败处理
		 */
		public void onFailure(Throwable error, String content) {
			error.printStackTrace();
			if (null != BaseHttpHandler.this.callback) {
				if ((error instanceof UnknownHostException)) {
					BaseHttpHandler.this.callback.onFailure(-3, ErrorCodeTransfer.getErrorMsg(-3), null);
				} else {
					BaseHttpHandler.this.callback.onFailure(-2, ErrorCodeTransfer.getErrorMsg(-2), null);
				}
			}
		}
	};

	/**
	 * 构造函数
	 * @param debugMode
	 * @param callback
	 */
	public BaseHttpHandler(boolean debugMode, HttpCallback callback) {
		this.TAG = logTag();
		if (debugMode)
			this.url = getDebugUrl();
		else {
			this.url = getReleaseUrl();
		}

		this.callback = callback;
		try {
			this.asyncHttpClient = AsyncHttpClient.getInstance();
			this.asyncHttpClient.setTimeout(30000);
		} catch (Exception e) {
			e.printStackTrace();

			if (null != callback) {
				callback.onFailure(-4, e.getMessage(), null);
			}
		}
	}


	public void addHeader(String header, String value) {
		this.asyncHttpClient.addHeader(header, value);
	}

	/**
	 * post
	 */
	public void post() {
		
		prepareRequest();
		
		Logger.d(this.TAG, "Post url:" + this.url + "\n" + "params:" + this.params);
		
		try {
			this.asyncHttpClient.post(this.url, this.params, this.responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
			if ((e instanceof UnknownHostException)) {
				if (null != this.callback) {
					this.callback.onFailure(-2, ErrorCodeTransfer.getErrorMsg(-3), null);
				}
			} else if (null != this.callback) {
				this.callback.onFailure(-2, ErrorCodeTransfer.getErrorMsg(-2), null);
			}
		}
	}

	/**
	 * get
	 */
	public void get() {
		prepareRequest();
		try {
			Logger.d(this.TAG, "Get url:" + this.url + "\n" + "params:" + this.params);
			this.asyncHttpClient.get(this.url, this.params, this.responseHandler);
		} catch (Exception e) {
			e.printStackTrace();

			if (null != this.callback) {
				this.callback.onFailure(-4, e.getMessage(), null);
			}
		}
	}

	public void prepareRequest() {
		initParams();
		prepareRequestOther();
	}

	private void initParams() {
		this.params = new RequestParams();
	}

	protected void setParam(String key, int value) {
		if (null == this.params) {
			initParams();
		}
		this.params.put(key, value + "");
	}

	protected void setParam(String key, String value) {
		if (null == this.params) {
			initParams();
		}

		this.params.put(key, value);
	}

	protected void setParam(String key, InputStream ins) {
		if (null == this.params) {
			initParams();
		}

		this.params.put(key, ins);
	}

	protected void setParam(String key, InputStream ins, String filename) {
		if (null == this.params) {
			initParams();
		}

		this.params.put(key, ins, filename);
	}

	protected void setParam(String key, InputStream ins, String filename, String contentType) {
		if (null == this.params) {
			initParams();
		}

		this.params.put(key, ins, filename, contentType);
	}

	protected void removeParam(String param) {
		this.params.remove(param);
	}

	public abstract void prepareRequestOther();

	public abstract ResponseEntity parseData(String paramString);
	
	public abstract String getDebugUrl();

	public abstract String getReleaseUrl();

	public abstract String logTag();
}
