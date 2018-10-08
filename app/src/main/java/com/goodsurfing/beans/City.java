package com.goodsurfing.beans;

/**
 * @ClassName: User
 * @Description: 用户登录信息实体
 * @version: 1.0
 * @author:
 * @Create: 2015-12-16
 */
public class City {


	/**
	 * id : 37
	 * provinceid : 18
	 * serverip : 119.39.127.250
	 * serverport : 8888
	 * ProvinceName : 湖南
	 */

	private String id;
	private String provinceid;
	private String serverip;
	private String serverport;
	private String ProvinceName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvinceid() {
		return provinceid;
	}

	public void setProvinceid(String provinceid) {
		this.provinceid = provinceid;
	}

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public String getServerport() {
		return serverport;
	}

	public void setServerport(String serverport) {
		this.serverport = serverport;
	}

	public String getProvinceName() {
		return ProvinceName;
	}

	public void setProvinceName(String ProvinceName) {
		this.ProvinceName = ProvinceName;
	}
}
