package com.reign.ast.sdk.http.async;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

/**
 * retry handler
 * @author zhouwenjia
 *
 */
class RetryHandler implements HttpRequestRetryHandler {
	private static final int RETRY_SLEEP_TIME_MILLIS = 1500;
	private static HashSet<Class<?>> exceptionWhitelist = new HashSet<Class<?>>();
	private static HashSet<Class<?>> exceptionBlacklist = new HashSet<Class<?>>();
	private final int maxRetries;

	static {
		exceptionWhitelist.add(NoHttpResponseException.class);
		exceptionWhitelist.add(UnknownHostException.class);
		exceptionWhitelist.add(SocketException.class);
		exceptionBlacklist.add(InterruptedIOException.class);
		exceptionBlacklist.add(SSLHandshakeException.class);
	}

	/**
	 * 构造函数
	 * @param maxRetries
	 */
	public RetryHandler(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	/**
	 * 重试request
	 */
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		Boolean b = (Boolean) context.getAttribute("http.request_sent");
		boolean sent = (b != null) && (b.booleanValue());
		boolean retry;
		if (executionCount > this.maxRetries) {
			retry = false;
		} else {
			if (exceptionBlacklist.contains(exception.getClass())) {
				retry = false;
			} else {
				if (exceptionWhitelist.contains(exception.getClass())) {
					retry = true;
				} else {
					if (!sent) {
						retry = true;
					} else {
						HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute("http.request");
						String requestType = currentReq.getMethod();
						if (!requestType.equals("POST")) {
							retry = true;
						} else {
							retry = false;
						}
					}
				}
			}
		}
		if (retry) {
			SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
		} else {
			exception.printStackTrace();
		}

		return retry;
	}
}
