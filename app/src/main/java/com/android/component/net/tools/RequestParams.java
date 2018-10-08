package com.android.component.net.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-10-20上午10:12:21
 * @version 1.0.0
 * @company 北京易微网络技术有限公司 Copyright: 版权所有 (c) 2014
 */
public class RequestParams implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1863256803210706847L;

	private List<Object> mParams;

	public RequestParams() {
		this.mParams = new ArrayList<Object>();
	}

	public RequestParams(List<Object> params) {
		this.mParams = params;
		if (params == null)
			this.mParams = new ArrayList<Object>();
	}

	public void put(Object value) {
		mParams.add(value);
	}

	public void put(List<Object> value) {
		if (value != null && value.size() > 0)
			mParams.addAll(value);
	}

	public byte[] getParams() {
		ByteOutputStream bos = new ByteOutputStream();
		bos.write(mParams);
		return bos.getBytes();
	}
}
