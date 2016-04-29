package com.reign.ast.sdk.http.async;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public class SimpleMultipartEntity implements HttpEntity {
	private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	private String boundary = null;

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	boolean isSetLast = false;
	boolean isSetFirst = false;

	public SimpleMultipartEntity() {
		StringBuffer buf = new StringBuffer();
		Random rand = new Random();
		for (int i = 0; i < 30; i++) {
			buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = buf.toString();
	}

	public void writeFirstBoundaryIfNeeds() {
		if (!this.isSetFirst) {
			try {
				this.out.write(("--" + this.boundary + "\r\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.isSetFirst = true;
	}

	public void writeLastBoundaryIfNeeds() {
		if (this.isSetLast) {
			return;
		}
		try {
			this.out.write(("\r\n--" + this.boundary + "--\r\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.isSetLast = true;
	}

	public void addPart(String key, String value) {
		writeFirstBoundaryIfNeeds();
		try {
			this.out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
			this.out.write(value.getBytes());
			this.out.write(("\r\n--" + this.boundary + "\r\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addPart(String key, String fileName, InputStream fin, boolean isLast) {
		addPart(key, fileName, fin, "application/octet-stream", isLast);
	}

	public void addPart(String key, String fileName, InputStream fin, String type, boolean isLast) {
		writeFirstBoundaryIfNeeds();
		try {
			type = "Content-Type: " + type + "\r\n";
			this.out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
			this.out.write(type.getBytes());
			this.out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

			byte[] tmp = new byte[4096];
			int l = 0;
			while ((l = fin.read(tmp)) != -1) {
				this.out.write(tmp, 0, l);
			}
			if (!isLast) {
				this.out.write(("\r\n--" + this.boundary + "\r\n").getBytes());
			}
			this.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				fin.close();
			} catch (IOException ee) {
				ee.printStackTrace();
			}
		} finally {
			try {
				fin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addPart(String key, File value, boolean isLast) {
		try {
			addPart(key, value.getName(), new FileInputStream(value), isLast);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public long getContentLength() {
		writeLastBoundaryIfNeeds();
		return this.out.toByteArray().length;
	}

	public Header getContentType() {
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + this.boundary);
	}

	public boolean isChunked() {
		return false;
	}

	public boolean isRepeatable() {
		return false;
	}

	public boolean isStreaming() {
		return false;
	}

	public void writeTo(OutputStream outstream) throws IOException {
		outstream.write(this.out.toByteArray());
	}

	public Header getContentEncoding() {
		return null;
	}

	public void consumeContent() throws IOException, UnsupportedOperationException {
		if (isStreaming()) {
			throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
		}
	}

	public InputStream getContent() throws IOException, UnsupportedOperationException {
		return new ByteArrayInputStream(this.out.toByteArray());
	}
}
