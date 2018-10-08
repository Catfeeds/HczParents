package com.android.component.net.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-10-20上午11:50:38
 * @version 1.0.0
 * @company 北京易微网络技术有限公司 Copyright: 版权所有 (c) 2014
 */
public class ByteInputStream {

	public byte[] buf;// 缓冲池
	private int pos = 0;// 指针
	private int len;

	public ByteInputStream() {
	}

	public ByteInputStream(byte[] buf) {
		if (null == buf)
			return;
		this.buf = buf;
		this.len = buf.length;
	}

	public void setDate(byte[] buf) {
		this.buf = buf;
		this.len = buf.length;
		this.pos = 0;

	}

	/**
	 * 跳过N个字节
	 * 
	 * @param length
	 */
	public void skip(int length) {
		this.pos += length;
	}

	public void reset() {
		this.pos = 0;
	}

	public int length() {
		return this.len;
	}

	public int remain() {
		return this.len - this.pos;
	}

	/**
	 * 读取一个字节
	 * 
	 * @return
	 */
	public byte readByte() {
		if (null == buf)
			return -1;
		return (byte) (buf[pos++] & 0xff);
	}

	/**
	 * 读取short
	 * 
	 * @return
	 */
	public short readShort() {
		if (null == buf)
			return -1;
		short h = (short) (buf[pos++] << 8 & 0xff00);
		short l = (short) (buf[pos++] & 0x00ff);
		return (short) ((h | l) & 0xffff);
	}

	/**
	 * 读取int32
	 * 
	 * @return
	 */
	public int readInt() {
		int h = readShort() << 16 & 0xffff0000;
		int l = readShort() & 0x0000ffff;
		return (h | l) & 0xffffffff;
	}

	/**
	 * 读取字节流 if it is fail,it will return null
	 * 
	 * @return
	 */
	public byte[] readByteStream() {
		try {
			int len = readInt();
			if (len > this.remain()) {
				return null;
			}
			byte[] result = new byte[len];
			ByteArrayInputStream in = new ByteArrayInputStream(buf, pos, len);
			in.read(result);
			pos += len;
			return result;
		} catch (IOException e) {
			return null;
		}
	}

	public String readString() {
		int len = readInt();// 字符串长度
		if (len > this.remain()) {
			return "";
		}
		byte[] tmp = new byte[len];

		for (int i = 0; i < len && pos < this.len; i++) {
			tmp[i] = buf[pos++];
		}
		try {
			return new String(tmp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			return "";
		}
	}
}
