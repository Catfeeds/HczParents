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

	public ExpireBean(String expiredate, String msg) {
		this.expiredate = expiredate;
		this.msg = msg;
	}

	public ExpireBean(boolean notice, String expiredate, String msg, String paylink) {
		this.notice = notice;
		this.expiredate = expiredate;
		this.msg = msg;
		this.paylink = paylink;
	}

	public ExpireBean() {
	}

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
