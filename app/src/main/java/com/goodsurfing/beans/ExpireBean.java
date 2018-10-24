package com.goodsurfing.beans;

public class ExpireBean {


	/**
	 * notice : true
	 * expiredate : 2018-10-26 11:29:14
	 * msg : 等下告诉你
	 * paylink : https:////www.haoup.net/
	 */

	private boolean notice;
	private String expiredate;
	private String msg;
	private String paylink;

	public boolean isNotice() {
		return notice;
	}

	public void setNotice(boolean notice) {
		this.notice = notice;
	}

	public String getExpiredate() {
		return expiredate;
	}

	public void setExpiredate(String expiredate) {
		this.expiredate = expiredate;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getPaylink() {
		return paylink;
	}

	public void setPaylink(String paylink) {
		this.paylink = paylink;
	}
}
