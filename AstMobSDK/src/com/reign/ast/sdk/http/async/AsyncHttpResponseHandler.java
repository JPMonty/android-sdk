package com.reign.ast.sdk.http.async;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 异步httpResponse handler
 * @author zhouwenjia
 *
 */
public class AsyncHttpResponseHandler {
	protected static final int SUCCESS_MESSAGE = 0;
	protected static final int FAILURE_MESSAGE = 1;
	protected static final int START_MESSAGE = 2;
	protected static final int FINISH_MESSAGE = 3;
	private Handler handler;

	/**
	 * 构造函数
	 */
	public AsyncHttpResponseHandler() {
		if (null != Looper.myLooper()) {
			this.handler = new Handler() {
				public void handleMessage(Message msg) {
					AsyncHttpResponseHandler.this.handleMessage(msg);
				}
			};
		}
	}

	public void onStart() {
	}

	public void onFinish() {
	}

	public void onSuccess(String content) {
	}

	public void onFailure(Throwable error) {
	}

	public void onFailure(Throwable error, String content) {
		onFailure(error);
	}

	/**
	 * 发送成功消息
	 * @param responseBody
	 */
	protected void sendSuccessMessage(String responseBody) {
		sendMessage(obtainMessage(0, responseBody));
	}

	/**
	 * 发送失败消息
	 * @param e
	 * @param responseBody
	 * @param url
	 */
	protected void sendFailureMessage(Throwable e, String responseBody, String url) {
		sendMessage(obtainMessage(1, new Object[] { e, responseBody, url }));
	}

	/**
	 * 发送失败消息
	 * @param e
	 * @param responseBody
	 */
	protected void sendFailureMessage(Throwable e, byte[] responseBody) {
		sendMessage(obtainMessage(1, new Object[] { e, responseBody }));
	}

	/**
	 * 发送开始消息
	 */
	protected void sendStartMessage() {
		sendMessage(obtainMessage(2, null));
	}

	/**
	 * 发送结束消息
	 */
	protected void sendFinishMessage() {
		sendMessage(obtainMessage(3, null));
	}

	protected void handleSuccessMessage(String responseBody) {
		onSuccess(responseBody);
	}

	protected void handleFailureMessage(Throwable e, String responseBody) {
		onFailure(e, responseBody);
	}

	/**
	 * 处理消息
	 * @param msg
	 */
	protected void handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			handleSuccessMessage((String) msg.obj);
			break;
		case 1:
			Object[] repsonse = (Object[]) msg.obj;
			handleFailureMessage((Throwable) repsonse[0], (String) repsonse[1]
					+ (repsonse.length == 3 ? (String) repsonse[2] : " & url is null"));
			break;
		case 2:
			onStart();
			break;
		case 3:
			onFinish();
		}
	}

	/**
	 * 发送消息
	 * @param msg
	 */
	protected void sendMessage(Message msg) {
		if (this.handler != null) {
			this.handler.sendMessage(msg);
		} else {
			handleMessage(msg);
		}
	}

	/**
	 * 获得消息
	 * @param responseMessage
	 * @param response
	 * @return
	 */
	protected Message obtainMessage(int responseMessage, Object response) {
		Message msg = null;
		if (this.handler != null) {
			msg = this.handler.obtainMessage(responseMessage, response);
		} else {
			msg = new Message();
			msg.what = responseMessage;
			msg.obj = response;
		}
		return msg;
	}

	/**
	 * 发送response消息
	 * @param request
	 * @param response
	 */
	void sendResponseMessage(HttpUriRequest request, HttpResponse response) {
		StatusLine status = response.getStatusLine();
		String responseBody = null;
		try {
			HttpEntity entity = null;
			HttpEntity temp = response.getEntity();
			if (null != temp) {
				entity = new BufferedHttpEntity(temp);
				responseBody = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (IOException e) {
			sendFailureMessage(e, null, request.getURI().toString());
		}

		if (status.getStatusCode() >= 300) {
			sendFailureMessage(new HttpResponseException(
					status.getStatusCode(), status.getReasonPhrase()),
					responseBody, request.getURI().toString());
		} else {
			sendSuccessMessage(responseBody);
		}
	}
}
