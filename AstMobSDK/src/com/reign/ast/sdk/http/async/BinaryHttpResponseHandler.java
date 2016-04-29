package com.reign.ast.sdk.http.async;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Message;

/**
 * 二进制httpResponse handler
 * @author zhouwenjia
 *
 */
public class BinaryHttpResponseHandler extends AsyncHttpResponseHandler {
	private static String[] mAllowedContentTypes = { "image/jpeg", "image/png" };

	/**
	 * 构造函数
	 */
	public BinaryHttpResponseHandler() {
	}

	/**
	 * 构造函数
	 * @param allowedContentTypes
	 */
	public BinaryHttpResponseHandler(String[] allowedContentTypes) {
		this();
		mAllowedContentTypes = allowedContentTypes;
	}

	public void onSuccess(byte[] binaryData) {
	}

	public void onFailure(Throwable error, byte[] binaryData) {
		onFailure(error);
	}

	/**
	 * 发送成功消息
	 * @param responseBody
	 */
	protected void sendSuccessMessage(byte[] responseBody) {
		sendMessage(obtainMessage(0, responseBody));
	}

	/**
	 * 发送失败消息
	 */
	protected void sendFailureMessage(Throwable e, byte[] responseBody) {
		sendMessage(obtainMessage(1, new Object[] { e, responseBody }));
	}

	/**
	 * 处理成功消息
	 * @param responseBody
	 */
	protected void handleSuccessMessage(byte[] responseBody) {
		onSuccess(responseBody);
	}

	/**
	 * 处理失败消息
	 * @param e
	 * @param responseBody
	 */
	protected void handleFailureMessage(Throwable e, byte[] responseBody) {
		onFailure(e, responseBody);
	}

	/**
	 * 处理消息
	 */
	protected void handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			handleSuccessMessage((byte[]) msg.obj);
			break;
		case 1:
			Object[] response = (Object[]) msg.obj;
			handleFailureMessage((Throwable) response[0], (byte[]) response[1]);
			break;
		default:
			super.handleMessage(msg);
		}
	}

	/**
	 * 发送response消息
	 * @param response
	 */
	void sendResponseMessage(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		Header[] contentTypeHeaders = response.getHeaders("Content-Type");
		byte[] responseBody = null;
		if (contentTypeHeaders.length != 1) {
			sendFailureMessage(new HttpResponseException(
					status.getStatusCode(),
					"None, or more than one, Content-Type Header found!"),
					responseBody);
			return;
		}
		Header contentTypeHeader = contentTypeHeaders[0];
		boolean foundAllowedContentType = false;
		for (String anAllowedContentType : mAllowedContentTypes) {
			if (anAllowedContentType.equals(contentTypeHeader.getValue())) {
				foundAllowedContentType = true;
			}
		}
		if (!foundAllowedContentType) {
			sendFailureMessage(new HttpResponseException(
					status.getStatusCode(), "Content-Type not allowed!"),
					responseBody);
			return;
		}
		try {
			HttpEntity entity = null;
			HttpEntity temp = response.getEntity();
			if (null != temp) {
				entity = new BufferedHttpEntity(temp);
			}
			responseBody = EntityUtils.toByteArray(entity);
		} catch (IOException e) {
			sendFailureMessage(e, null);
		}

		if (status.getStatusCode() >= 300) {
			sendFailureMessage(new HttpResponseException(
					status.getStatusCode(), status.getReasonPhrase()),
					responseBody);
		} else {
			sendSuccessMessage(responseBody);
		}
	}
}
