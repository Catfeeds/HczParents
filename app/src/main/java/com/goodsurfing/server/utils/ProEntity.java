package com.goodsurfing.server.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.ByteArrayEntity;

import com.goodsurfing.server.utils.BaseDataService.UploadProgress;

public class ProEntity extends ByteArrayEntity {
	private final UploadProgress listener;
	private int length = 2048;

	public ProEntity(byte[] b, UploadProgress listner) {
		super(b);
		this.listener = listner;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}

	public interface MProgrListener {
		void transferred(long num, long total);
	}

	public class CountingOutputStream extends FilterOutputStream {

		private final UploadProgress listener;

		public CountingOutputStream(final OutputStream out,
				final UploadProgress listener) {
			super(out);
			this.listener = listener;
		}

		public void write(byte[] b, int off, int lens) throws IOException {

			int len = 0;
			while (len <= b.length) {
				int count = length;
				if (len + length >= b.length) {
					count = b.length - len;
				}
				out.write(b, len, count);
				listener.transferred(len, b.length);
				len += length;
			}
		}
	}

}
