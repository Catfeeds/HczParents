package com.goodsurfing.beans;

import java.io.Serializable;

import android.text.TextUtils;

/**
 * @ClassName: User
 * @Description: 用户登录信息实体
 * @version: 1.0
 * @author:
 * @Create: 2015-12-16
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uId=""; // 用户编号
	private String userName=""; // 会员名
	private String sId=""; // session编号
	private String phone=""; // 注册手机号
	private String avatar=""; // 头像地址
	private String account="";// 用户账号
	private String password="";// 权限密码
	private String groupId="";// 组id
	private String tokenId="";// 唯一标识 用户名+时间戳
	private String email="";// 邮箱
	private String type="";// 用户类型，try为试用用户，pay为付费用户
	private String status="";// 当前用户状态：\n-1：欠费状态2:停止使用\n1:正常使用中
	private String autoState="";// 授权状态0-未授权，1-授权
	private String mode=""; // 1家长、2学生、3黄赌毒、4计时、5去广告
	private String customType="";// 在自定义模式下生效，1表示仅允许白名单 2. 仅过滤黑名单
	private String IP="";// ip
	private String startPort="";//
	private String endPort="";
	private String editTime="";
	private String userSex="";// 用户性别
	private String identityCard="";// 用户身份证
	private String address="";// 家庭住址
	private String edit_phone_code_url="";// 家庭住址
	private String loginTime="";// 登录时间
	private String rewardCode="";

	private volatile static User user;

	private User() {
	}

	public static User getUser() {
		if (user == null) {
					user = new User();
		}
		return user;
	}

	public String getEdit_phone_code_url() {
		return edit_phone_code_url == null ? "" : edit_phone_code_url;
	}

	public void setEdit_phone_code_url(String edit_phone_code_url) {
		this.edit_phone_code_url = edit_phone_code_url;
	}

	public String getRewardCode() {
		return rewardCode;
	}

	public void setRewardCode(String rewardCode) {
		this.rewardCode = rewardCode;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		if (!TextUtils.isEmpty(avatar)) {
			avatar = avatar.replace("\\", "");
		}
		this.avatar = avatar;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getsId() {
		sId = sId.replace("+", "@");
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAutoState() {
		return autoState;
	}

	public void setAutoState(String autoState) {
		this.autoState = autoState;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getCustomType() {
		return customType;
	}

	public void setCustomType(String customType) {
		this.customType = customType;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getStartPort() {
		return startPort;
	}

	public void setStartPort(String startPort) {
		this.startPort = startPort;
	}

	public String getEndPort() {
		return endPort;
	}

	public void setEndPort(String endPort) {
		this.endPort = endPort;
	}

	public String getEditTime() {
		return editTime;
	}

	public void setEditTime(String editTime) {
		this.editTime = editTime;
	}

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

}
