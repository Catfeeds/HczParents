package com.goodsurfing.beans;

public class FuncBean {


	/**
	 * funcName : 功能名
	 * status : 0 开通状态 1是开通
	 */

	private String funcName;
	private String status;

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public  boolean isOpen(){
		return "1".equals(status);
	}
}
