package com.goodsurfing.beans;

import java.io.Serializable;

public class WebFilterBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private long _id;
	private String id;
	private String webTitle;
	private String webSite;
	private String webTye;
	private String webCreateTime;
	private String webStatus;
	private String isDeleting;
	private boolean isSelected = false;
	private String webClassType;
	private String reason;
	private String icon;
	
	
	
	public WebFilterBean() {
	}


	public WebFilterBean(String id, String webTitle, String webSite, String webTye, String webCreateTime, String webStatus, String isDeleting, boolean isSelected, String webClassType, String reason, String icon) {
		this.id = id;
		this.webTitle = webTitle;
		this.webSite = webSite;
		this.webTye = webTye;
		this.webCreateTime = webCreateTime;
		this.webStatus = webStatus;
		this.isDeleting = isDeleting;
		this.isSelected = isSelected;
		this.webClassType = webClassType;
		this.reason = reason;
		this.icon = icon;
	}
	

	public long get_id() {
		return _id;
	}


	public void set_id(long _id) {
		this._id = _id;
	}


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}


	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getIsDeleting() {
		return isDeleting;
	}

	public void setIsDeleting(String isDeleting) {
		this.isDeleting = isDeleting;
	}

	public String getWebTitle() {
		return webTitle;
	}

	public void setWebTitle(String webTitle) {
		this.webTitle = webTitle;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getWebTye() {
		return webTye;
	}

	public void setWebTye(String webTye) {
		this.webTye = webTye;
	}

	public String getWebCreateTime() {
		return webCreateTime;
	}

	public void setWebCreateTime(String webCreateTime) {
		this.webCreateTime = webCreateTime;
	}

	public String getWebStatus() {
		return webStatus;
	}

	public void setWebStatus(String webStatus) {
		this.webStatus = webStatus;
	}

	public String getWebClassType() {
		return webClassType;
	}

	public void setWebClassType(String webClassType) {
		this.webClassType = webClassType;
	}

}
