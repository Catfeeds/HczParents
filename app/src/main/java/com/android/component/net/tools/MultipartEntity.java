package com.android.component.net.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MultipartEntity {
	private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	private String boundary = null;

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	boolean isSetLast = false;
	boolean isSetFirst = false;

	public MultipartEntity() {
		StringBuffer localStringBuffer = new StringBuffer();
		Random localRandom = new Random();
		for (int i = 0; i < 30; i++) {
			localStringBuffer.append(MULTIPART_CHARS[localRandom
					.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = localStringBuffer.toString();
	}

	public void writeFirstBoundaryIfNeeds() {
		if (!this.isSetFirst) {
			try {
				this.out.write(("--" + this.boundary + "\r\n").getBytes());
			} catch (IOException localIOException) {
				localIOException.printStackTrace();
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
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		this.isSetLast = true;
	}

	public void addPart(String paramString1, String paramString2) {
		writeFirstBoundaryIfNeeds();
		try {
			this.out.write(("Content-Disposition: form-data; name=\""
					+ paramString1 + "\"\r\n\r\n").getBytes());
			this.out.write(paramString2.getBytes());
			this.out.write(("\r\n--" + this.boundary + "\r\n").getBytes());
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
	}

	public void addPart(String paramString1, String paramString2,
			InputStream paramInputStream, boolean paramBoolean) {
		addPart(paramString1, paramString2, paramInputStream,
				"application/octet-stream", paramBoolean);
	}

	public void addPart(String paramString1, String paramString2,
			InputStream paramInputStream, String paramString3,
			boolean paramBoolean) {
		writeFirstBoundaryIfNeeds();
		try {
			paramString3 = "Content-Type: " + paramString3 + "\r\n";
			this.out.write(("Content-Disposition: form-data; name=\""
					+ paramString1 + "\"; filename=\"" + paramString2 + "\"\r\n")
					.getBytes());
			this.out.write(paramString3.getBytes());
			this.out.write("Content-Transfer-Encoding: binary\r\n\r\n"
					.getBytes());

			byte[] arrayOfByte = new byte[4096];
			int i = 0;
			while ((i = paramInputStream.read(arrayOfByte)) != -1) {
				this.out.write(arrayOfByte, 0, i);
			}
			if (!paramBoolean)
				this.out.write(("\r\n--" + this.boundary + "\r\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				paramInputStream.close();
			} catch (IOException localIOException4) {
				localIOException4.printStackTrace();
			}
		}
	}

	public void addPart(String paramString, File paramFile, boolean paramBoolean) {
		try {
			addPart(paramString, paramFile.getName(), new FileInputStream(
					paramFile), paramBoolean);
		} catch (FileNotFoundException localFileNotFoundException) {
			localFileNotFoundException.printStackTrace();
		}
	}

	public byte[] getContent() {
		byte[] content = null;
		try {
			this.out.write(("\r\n--" + this.boundary + "--\r\n").getBytes());
			content = this.out.toByteArray();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String getBoundary() {
		return boundary;
	}
}