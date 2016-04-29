package com.reign.ast.sdk.http.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * 请求参数
 * @author zhouwenjia
 *
 */
public class RequestParams {
	
	private static String ENCODING = "UTF-8";
	protected ConcurrentHashMap<String, String> urlParams;
	protected ConcurrentHashMap<String, FileWrapper> fileParams;

	/**
	 * 构造函数
	 */
	public RequestParams() {
		init();
	}

	/**
	 * 构造函数
	 * @param source
	 */
	public RequestParams(Map<String, String> source) {
		init();

		for (Entry<String, String> entry : source.entrySet()) {
			put((String) entry.getKey(), (String) entry.getValue());
		}
	}

	/**
	 * 构造函数
	 * @param key
	 * @param value
	 */
	public RequestParams(String key, String value) {
		init();

		put(key, value);
	}

	/**
	 * 填充参数
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		if ((key != null) && (value != null)) {
			this.urlParams.put(key, value);
		}
	}

	public void put(String key, File file) throws FileNotFoundException {
		put(key, new FileInputStream(file), file.getName());
	}

	public void put(String key, InputStream stream) {
		put(key, stream, null);
	}

	public void put(String key, InputStream stream, String fileName) {
		put(key, stream, fileName, null);
	}

	public void put(String key, InputStream stream, String fileName, String contentType) {
		if ((key != null) && (stream != null)) {
			this.fileParams.put(key, new FileWrapper(stream, fileName,contentType));
		}
	}

	/**
	 * 移除key
	 * @param key
	 */
	public void remove(String key) {
		this.urlParams.remove(key);
		this.fileParams.remove(key);
	}

	/**
	 * toString
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Entry<String, String> entry : this.urlParams.entrySet()) {
			if (result.length() > 0) {
				result.append("&");
			}
			result.append((String) entry.getKey());
			result.append("=");
			result.append((String) entry.getValue());
		}

		for (Entry<String, FileWrapper> entry : this.fileParams.entrySet()) {
			if (result.length() > 0) {
				result.append("&");
			}
			result.append((String) entry.getKey());
			result.append("=");
			result.append("FILE");
		}

		return result.toString();
	}

	/**
	 * 获得entity
	 * @return
	 */
	public HttpEntity getEntity() {
		HttpEntity entity = null;

		if (!this.fileParams.isEmpty()) {
			SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();

			for (Entry<String, String> entry : this.urlParams.entrySet()) {
				multipartEntity.addPart((String) entry.getKey(), (String) entry.getValue());
			}

			int currentIndex = 0;
			int lastIndex = this.fileParams.entrySet().size() - 1;
			for (Entry<String, FileWrapper> entry : this.fileParams.entrySet()) {
				FileWrapper file = (FileWrapper) entry.getValue();
				if (file.inputStream != null) {
					boolean isLast = currentIndex == lastIndex;
					if (null != file.contentType) {
						multipartEntity.addPart((String) entry.getKey(), file.getFileName(), file.inputStream, file.contentType, isLast);
					} else {
						multipartEntity.addPart((String) entry.getKey(), file.getFileName(), file.inputStream, isLast);
					}
				}
				currentIndex++;
			}

			entity = multipartEntity;
		} else {
			try {
				entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return entity;
	}

	/**
	 * 初始化
	 */
	private void init() {
		this.urlParams = new ConcurrentHashMap<String, String>();
		this.fileParams = new ConcurrentHashMap<String, FileWrapper>();
	}

	/**
	 * 获得参数列表
	 * @return
	 */
	protected List<BasicNameValuePair> getParamsList() {
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

		for (Entry<String, String> entry : this.urlParams.entrySet()) {
			lparams.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
		}
		Collections.sort(lparams, new ParamComparator());
		return lparams;
	}

	/**
	 * 获得参数串
	 * @return
	 */
	protected String getParamString() {
		return URLEncodedUtils.format(getParamsList(), ENCODING);
	}

	/**
	 * file包装器
	 * @author zhouwenjia
	 *
	 */
	private static class FileWrapper {
		public InputStream inputStream;
		public String fileName;
		public String contentType;

		public FileWrapper(InputStream inputStream, String fileName,
				String contentType) {
			this.inputStream = inputStream;
			this.fileName = fileName;
			this.contentType = contentType;
		}

		public String getFileName() {
			if (this.fileName != null) {
				return this.fileName;
			}
			return "nofilename";
		}
	}

	/**
	 * 参数比较器
	 * @author zhouwenjia
	 *
	 */
	class ParamComparator implements Comparator<Object> {
		ParamComparator() {
		}

		public int compare(Object lhs, Object rhs) {
			String lkey = ((BasicNameValuePair) lhs).getName();
			String rkey = ((BasicNameValuePair) rhs).getName();

			return lkey.compareToIgnoreCase(rkey);
		}
	}
}
