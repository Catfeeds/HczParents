package com.android.component.net;

/**
 * 
 * @author 谢志杰 
 * @create 2016-10-9 下午2:12:13 
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有（c）2016
 */
public class NetResult {
	/**
	 * 状态码
	 */
	private int state;
	/**
	 * 数据
	 */
	private String data;
	/**
	 * 错误信息
	 */
	private String errorMsg;
	
	/**
	 * 消息队列
	 */
	private int num;
	public NetResult() {
	};

	public NetResult(int state, String errorMsg, String data) {
		this.state = state;
		this.errorMsg = errorMsg;
		this.data = data;
	}
	

	public NetResult(int state,  String errorMsg,String data, int num) {
		super();
		this.state = state;
		this.data = data;
		this.errorMsg = errorMsg;
		this.num = num;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
