package com.reign.ast.sdk.http.async;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

/**
 * 异步http request
 * @author zhouwenjia
 *
 */
class AsyncHttpRequest implements Runnable {
	private final AbstractHttpClient client;
	private final HttpContext context;
	private final HttpUriRequest request;
	private final AsyncHttpResponseHandler responseHandler;
	private boolean isBinaryRequest;
	private int executionCount;

	/**
	 * 构造函数
	 * @param client
	 * @param context
	 * @param request
	 * @param responseHandler
	 */
	public AsyncHttpRequest(AbstractHttpClient client, HttpContext context,
			HttpUriRequest request, AsyncHttpResponseHandler responseHandler) {
		this.client = client;
		this.context = context;
		this.request = request;
		this.responseHandler = responseHandler;
		if ((responseHandler instanceof BinaryHttpResponseHandler)) {
			this.isBinaryRequest = true;
		}
	}

	/**
	 * run
	 */
	public void run() {
		try {
			if (this.responseHandler != null) {
				this.responseHandler.sendStartMessage();
			}

			makeRequestWithRetries();

			if (this.responseHandler != null) {
				this.responseHandler.sendFinishMessage();
			}
		} catch (IOException e) {
			if (this.responseHandler != null) {
				this.responseHandler.sendFinishMessage();
				if (this.isBinaryRequest) {
					this.responseHandler.sendFailureMessage(e, null);
				} else {
					this.responseHandler.sendFailureMessage(e, null, this.request.getURI().toString());
				}
			}
		}
	}

	/**
	 * 发送请求
	 * @throws IOException
	 */
	private void makeRequest() throws IOException {
		if (!Thread.currentThread().isInterrupted()) {
			HttpResponse response = this.client.execute(this.request, this.context);
			if ((!Thread.currentThread().isInterrupted()) && (this.responseHandler != null)) {
				this.responseHandler.sendResponseMessage(this.request, response);
			}
		}
	}

	/**
	 * 带重试的发送请求
	 * @throws ConnectException
	 */
	private void makeRequestWithRetries() throws ConnectException {
		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = this.client.getHttpRequestRetryHandler();
		while (retry) {
			try {
				makeRequest();
				return;
			} catch (UnknownHostException e) {
				if (this.responseHandler != null) {
					this.responseHandler.sendFailureMessage(e, "can't resolve host", this.request.getURI().toString());
				}
				return;
			} catch (IOException e) {
				cause = e;
				retry = retryHandler.retryRequest(cause, ++this.executionCount, this.context);
			} catch (NullPointerException e) {
				e.printStackTrace();

				cause = new IOException("NPE in HttpClient" + e.getMessage());
				retry = retryHandler.retryRequest(cause, ++this.executionCount, this.context);
			}

		}

		ConnectException ex = new ConnectException();
		ex.initCause(cause);
		throw ex;
	}
}
