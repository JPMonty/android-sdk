package com.reign.ast.sdk.http.async;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import android.content.Context;

/**
 * 异步httpClient
 * @author zhouwenjia
 *
 */
public class AsyncHttpClient {
	private static final String VERSION = "1.4.1";
	private static final int DEFAULT_MAX_CONNECTIONS = 10;
	private static final int DEFAULT_MAX_RETRIES = 5;
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";
	private static int maxConnections = 10;
	private static int socketTimeout = 10000;
	private final DefaultHttpClient httpClient;
	private final HttpContext httpContext;
	private ThreadPoolExecutor threadPool;
	private final Map<Context, List<WeakReference<Future<?>>>> requestMap;
	private final Map<String, String> clientHeaderMap;
	
	/** 单例 */
	private static AsyncHttpClient instance = new AsyncHttpClient();

	/**
	 * 获得单例
	 * @return
	 */
	public static AsyncHttpClient getInstance() {
		return instance;
	}

	/**
	 * 构造函数
	 */
	private AsyncHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();

		ConnManagerParams.setTimeout(httpParams, socketTimeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
		ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

		HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(httpParams, String.format(
				"android-async-http/%s (http://loopj.com/android-async-http)",
				new Object[] { VERSION }));

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		SSLSocketFactory factroy = SSLSocketFactoryEx.getSocketFactory();
		factroy.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		schemeRegistry.register(new Scheme("https", factroy, 443));
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

		this.httpContext = new SyncBasicHttpContext(new BasicHttpContext());

		this.httpClient = new DefaultHttpClient(cm, httpParams);
		this.httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String header : AsyncHttpClient.this.clientHeaderMap.keySet()) {
					request.addHeader(header, (String) AsyncHttpClient.this.clientHeaderMap.get(header));
				}
			}
		});
		this.httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(HttpResponse response, HttpContext context) {
				HttpEntity entity = response.getEntity();
				if (null == entity) {
					return;
				}
				Header encoding = entity.getContentEncoding();
				if (null != encoding) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new AsyncHttpClient.InflatingEntity(response.getEntity()));
							break;
						}
					}
				}
			}
		});
		this.httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_MAX_RETRIES));
		this.threadPool = ((ThreadPoolExecutor) Executors.newCachedThreadPool());
		this.requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();
		this.clientHeaderMap = new HashMap<String, String>();
	}

	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	public HttpContext getHttpContext() {
		return this.httpContext;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.httpContext.setAttribute("http.cookie-store", cookieStore);
	}

	public void setThreadPool(ThreadPoolExecutor threadPool) {
		this.threadPool = threadPool;
	}

	/**
	 * 设置userAgent
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent) {
		HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
	}

	/**
	 * 设置timeout
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		HttpParams httpParams = this.httpClient.getParams();
		ConnManagerParams.setTimeout(httpParams, timeout);
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
	}

	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sslSocketFactory, 443));
	}

	public void addHeader(String header, String value) {
		this.clientHeaderMap.put(header, value);
	}

	public void setBasicAuth(String user, String pass) {
		AuthScope scope = AuthScope.ANY;
		setBasicAuth(user, pass, scope);
	}

	public void setBasicAuth(String user, String pass, AuthScope scope) {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
		this.httpClient.getCredentialsProvider().setCredentials(scope, credentials);
	}

	/**
	 * 取消requests
	 * @param context
	 * @param mayInterruptIfRunning
	 */
	public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
		List<WeakReference<Future<?>>> requestList = this.requestMap.get(context);
		if (null != requestList) {
			for (WeakReference<Future<?>> requestRef : requestList) {
				Future<?> request = (Future<?>) requestRef.get();
				if (null != request) {
					request.cancel(mayInterruptIfRunning);
				}
			}
		}
		this.requestMap.remove(context);
	}

	public void get(String url, AsyncHttpResponseHandler responseHandler) {
		get(null, url, null, responseHandler);
	}

	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		get(null, url, params, responseHandler);
	}

	public void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
		get(context, url, null, responseHandler);
	}

	public void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		sendRequest(this.httpClient, this.httpContext, new HttpGet(getUrlWithQueryString(url, params)), null, responseHandler, context);
	}

	public void get(Context context, String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
		if (null != headers) {
			request.setHeaders(headers);
		}
		sendRequest(this.httpClient, this.httpContext, request, null, responseHandler, context);
	}

	public void post(String url, AsyncHttpResponseHandler responseHandler) {
		post(null, url, null, responseHandler);
	}

	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		post(null, url, params, responseHandler);
	}

	public void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		post(context, url, paramsToEntity(params), null, responseHandler);
	}

	public void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
		sendRequest(this.httpClient, this.httpContext,
				addEntityToRequestBase(new HttpPost(url), entity), 
				contentType, responseHandler, context);
	}

	/**
	 * post
	 * @param context
	 * @param url
	 * @param headers
	 * @param params
	 * @param contentType
	 * @param responseHandler
	 */
	public void post(Context context, String url, Header[] headers,
			RequestParams params, String contentType,
			AsyncHttpResponseHandler responseHandler) {
		HttpEntityEnclosingRequestBase request = new HttpPost(url);
		if (null != params) {
			request.setEntity(paramsToEntity(params));
		}
		if (null != headers) {
			request.setHeaders(headers);
		}
		sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
	}

	/**
	 * post
	 * @param context
	 * @param url
	 * @param headers
	 * @param entity
	 * @param contentType
	 * @param responseHandler
	 */
	public void post(Context context, String url, Header[] headers,
			HttpEntity entity, String contentType,
			AsyncHttpResponseHandler responseHandler) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(url), entity);
		if (null != headers) {
			request.setHeaders(headers);
		}
		sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
	}

	public void put(String url, AsyncHttpResponseHandler responseHandler) {
		put(null, url, null, responseHandler);
	}

	public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		put(null, url, params, responseHandler);
	}

	public void put(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		put(context, url, paramsToEntity(params), null, responseHandler);
	}

	public void put(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
		sendRequest(this.httpClient, this.httpContext,
				addEntityToRequestBase(new HttpPut(url), entity), 
				contentType, responseHandler, context);
	}

	public void put(Context context, String url, Header[] headers,
			HttpEntity entity, String contentType,
			AsyncHttpResponseHandler responseHandler) {
		HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPut(url), entity);
		if (headers != null) {
			request.setHeaders(headers);
		}
		sendRequest(this.httpClient, this.httpContext, request, contentType, responseHandler, context);
	}

	public void delete(String url, AsyncHttpResponseHandler responseHandler) {
		delete(null, url, responseHandler);
	}

	public void delete(Context context, String url, AsyncHttpResponseHandler responseHandler) {
		HttpDelete delete = new HttpDelete(url);
		sendRequest(this.httpClient, this.httpContext, delete, null, responseHandler, context);
	}

	public void delete(Context context, String url, Header[] headers, AsyncHttpResponseHandler responseHandler) {
		HttpDelete delete = new HttpDelete(url);
		if (headers != null) {
			delete.setHeaders(headers);
		}
		sendRequest(this.httpClient, this.httpContext, delete, null, responseHandler, context);
	}

	/**
	 * 发送请求
	 * @param client
	 * @param httpContext
	 * @param uriRequest
	 * @param contentType
	 * @param responseHandler
	 * @param context
	 */
	private void sendRequest(DefaultHttpClient client, HttpContext httpContext,
			HttpUriRequest uriRequest, String contentType,
			AsyncHttpResponseHandler responseHandler, Context context) {
		if (null != contentType) {
			uriRequest.addHeader("Content-Type", contentType);
		}

		Future<?> request = this.threadPool.submit(new AsyncHttpRequest(client, httpContext, uriRequest, responseHandler));

		if (null != context) {
			List<WeakReference<Future<?>>> requestList = this.requestMap.get(context);
			if (null == requestList) {
				requestList = new LinkedList<WeakReference<Future<?>>>();
				this.requestMap.put(context, requestList);
			}

			requestList.add(new WeakReference<Future<?>>(request));
		}
	}

	/**
	 * 获得queryString url
	 * @param url
	 * @param params
	 * @return
	 */
	private String getUrlWithQueryString(String url, RequestParams params) {
		if (null != params) {
			String paramString = params.getParamString();
			url = url + "?" + paramString;
		}
		return url;
	}

	private HttpEntity paramsToEntity(RequestParams params) {
		HttpEntity entity = null;

		if (null != params) {
			entity = params.getEntity();
		}
		return entity;
	}

	private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
		if (null != entity) {
			requestBase.setEntity(entity);
		}
		return requestBase;
	}

	/**
	 * inflating
	 * @author zhouwenjia
	 *
	 */
	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		public InputStream getContent() throws IOException {
			return new GZIPInputStream(this.wrappedEntity.getContent());
		}

		public long getContentLength() {
			return -1L;
		}
	}
}
