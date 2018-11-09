package com.goodsurfing.beans;

import com.goodsurfing.constants.Constants;

import java.io.Serializable;

public class ChildBean implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ClientDeviceId : 24
	 * UUID : 862949037554032e2de8fe16fb6137a
	 * ServerId : 29
	 * Name : null
	 * Img : null
	 * Mode : 3
	 * UpdatedAt : 2018-07-09 13:56:11
	 * CreatedAt : 2018-07-08 10:17:08
	 * Status : 1
	 * Battery : 0
	 * LockPwd :
	 * ModeTime : 0
	 * Mobile :  8615660229
	 * Device : OPPO_OPPO R9m
	 * Gender : 0
	 * System : 5.1
	 * openAppStatus : false
	 */

	private int ClientDeviceId;
	private String UUID;
	private int ServerId;
	private String Name;
	private int Img;
	private int Mode;
	private String UpdatedAt;
	private String CreatedAt;
	private String Status;
	private int Battery;
	private String LockPwd;
	private String ModeTime;
	private String Mobile;
	private String Device;
	private int Gender;
	private String System;
	private String NetType;
	private boolean openAppStatus;
	/**
	 * openDNS : 1
	 * locktime : 0
	 */

	private int openDNS;

	public ChildBean() {
	}

	public boolean isOpenAppStatus() {
		return openAppStatus;
	}

	public void setOpenAppStatus(boolean openAppStatus) {
		this.openAppStatus = openAppStatus;
	}

	public int getClientDeviceId() {
		return ClientDeviceId;
	}

	public void setClientDeviceId(int ClientDeviceId) {
		this.ClientDeviceId = ClientDeviceId;
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String UUID) {
		this.UUID = UUID;
	}

	public int getServerId() {
		return ServerId;
	}

	public String getNetType() {
		return NetType;
	}

	public void setNetType(String netType) {
		NetType = netType;
	}

	public void setServerId(int ServerId) {
		this.ServerId = ServerId;
	}

	public String getName() {
		return Name+"";
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public int getImg() {
		return Img% Constants.showIds.length;
	}

	public void setImg(int Img) {
		this.Img = Img;
	}

	public int getMode() {
		return Mode;
	}

	public void setMode(int Mode) {
		this.Mode = Mode;
	}

	public String getUpdatedAt() {
		return UpdatedAt;
	}

	public void setUpdatedAt(String UpdatedAt) {
		this.UpdatedAt = UpdatedAt;
	}

	public String getCreatedAt() {
		return CreatedAt;
	}

	public void setCreatedAt(String CreatedAt) {
		this.CreatedAt = CreatedAt;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String Status) {
		this.Status = Status;
	}

	public int getBattery() {
		return Battery;
	}

	public void setBattery(int Battery) {
		this.Battery = Battery;
	}

	public String getLockPwd() {
		return LockPwd;
	}

	public void setLockPwd(String LockPwd) {
		this.LockPwd = LockPwd;
	}

	public String getModeTime() {
		return ModeTime;
	}

	public void setModeTime(String ModeTime) {
		this.ModeTime = ModeTime;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String Mobile) {
		this.Mobile = Mobile;
	}

	public String getDevice() {
		return Device;
	}

	public void setDevice(String Device) {
		this.Device = Device;
	}

	public int getGender() {
		return Gender;
	}

	public void setGender(int Gender) {
		this.Gender = Gender;
	}

	public String getSystem() {
		return System;
	}

	public void setSystem(String System) {
		this.System = System;
	}

	public int getOpenDNS() {
		return openDNS;
	}

	public void setOpenDNS(int openDNS) {
		this.openDNS = openDNS;
	}
}
