package com.goodsurfing.beans;

import java.util.List;

/**
 * @ClassName: User
 * @Description: 用户登录信息实体
 * @version: 1.0
 * @author:
 * @Create: 2015-12-16
 */
public class IPList {


	/**
	 * id : 14
	 * name : 好上网手机
	 * isplist : [{"id":"59","provinceid":"35","serverip":"120.76.233.222","serverport":"9292","ProvinceName":"中国"}]
	 */

	private String id;
	private String name;
	private List<City> isplist;
	/**
	 * show_btn : 0
	 * open_url :
	 */

	private String show_btn;
	private String open_url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<City> getIsplist() {
		return isplist;
	}

	public void setIsplist(List<City> isplist) {
		this.isplist = isplist;
	}

	public String getShow_btn() {
		return show_btn;
	}

	public void setShow_btn(String show_btn) {
		this.show_btn = show_btn;
	}

	public String getOpen_url() {
		return open_url;
	}

	public void setOpen_url(String open_url) {
		this.open_url = open_url;
	}
}
