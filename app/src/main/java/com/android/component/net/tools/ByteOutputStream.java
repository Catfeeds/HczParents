package com.android.component.net.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-10-20上午10:20:48
 * @version 1.0.0
 * @company 北京易微网络技术有限公司 Copyright: 版权所有 (c) 2014
 */
public class ByteOutputStream {

	private ByteArrayOutputStream mOutputStream;

	public ByteOutputStream() {
		mOutputStream = new ByteArrayOutputStream();
	}

	public void write(List<Object> values) {
		if (values != null) {
			for (Object value : values) {
				write(value);
			}
		}
	}

	public void write(Object value) {
		if (value instanceof Byte) {
			writeByte((Byte) value);
		} else if (value instanceof Short) {
			writeShort((Short) value);
		} else if (value instanceof Integer) {
			writeInt((Integer) value);
		} else if (value instanceof Long) {
			writeLong((Long) value);
		} else if (value instanceof Double) {
			writeDouble((Double) value);
		} else if (value instanceof String || value == null) {
			writeString((String) value);
		} else if (value instanceof File) {
			writeFile((File) value);
		}
	}

	public void writeByte(byte value) {
		mOutputStream.write(value & 0xff);
	}

	public void writeInt(int value) {
		mOutputStream.write((byte) (value >>> 24 & 0xff));
		mOutputStream.write((byte) (value >>> 16 & 0xff));
		mOutputStream.write((byte) (value >>> 8 & 0xff));
		mOutputStream.write((byte) (value & 0xff));
	}

	public void writeShort(short value) {
		mOutputStream.write((byte) (value >>> 8 & 0xff));
		mOutputStream.write((byte) (value & 0xff));
	}

	public void writeLong(long value) {

	}

	public void writeFloat(float value) {

	}

	public void writeDouble(double value) {

	}

	public void writeString(String value) {
		if (value == null)
			value = "";
		byte[] tmp;
		try {
			tmp = value.getBytes("UTF-8");
			writeInt(tmp.length);
			mOutputStream.write(tmp);
		} catch (IOException e) {
		}
	}

	public void writeFile(File file) {
		if (null == file)
			return;
		byte[] value = getFileBytes(file);
		try {
			writeInt(value.length);
			mOutputStream.write(value);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
		}
	}

	public byte[] getFileBytes(File file) {
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			bos = new ByteArrayOutputStream();

			while ((len = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			byte[] data = bos.toByteArray();
			return data;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new byte[0];
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return new byte[0];
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] getBytes() {
		int length = mOutputStream.toByteArray().length;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteOutputStream bos = new ByteOutputStream();
		bos.setOutputStream(outputStream);
		bos.write(getHead(length));
		try {
			outputStream.write(mOutputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
		} finally {
			try {
				mOutputStream.close();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
			}
		}
		return outputStream.toByteArray();
	}

	public void setOutputStream(ByteArrayOutputStream outputStream) {
		this.mOutputStream = outputStream;
	}

	private List<Object> getHead(int length) {
		List<Object> head = new ArrayList<Object>();
		head.add(length);
//		head.add(TextUtils.isEmpty(BaseApplication.YWID) ? "0" : BaseApplication.YWID);
		head.add("M4");
		head.add("android" + android.os.Build.VERSION.RELEASE);
//		head.add(Constant.VERSION_CODE + "");
//		head.add(BaseApplication.deviceToken);
		return head;
	}
}
