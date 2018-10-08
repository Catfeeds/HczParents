package com.android.component.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description 字符串操作
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2016-8-21 上午11:40:27
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class StringUtil {

	public static final String TAG = "StringUtil";

	/**
	 * 字符串是否为空
	 * 
	 * @param value
	 * @return 字符串为""或Null或"null"返回true
	 */
	public static boolean isEmpty(String value) {
		boolean flag = false;
		if (value == null || "".equals(value) || "null".equals(value)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * MD5加密
	 * 
	 * @author 谢志杰
	 * @create 2015-7-2 上午10:10:44
	 * @param str
	 * @return
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}

	/**
	 * 删除转义符
	 * 
	 * @author 谢志杰
	 * @create 2015-6-26 上午9:51:50
	 * @param str
	 * @return
	 */
	public static String deleteEscape(String str) {
		char[] chars = str.toCharArray();
		String newStr = "";
		for (char ch : chars) {
			if (ch != 92) {
				newStr += ch;
			}
		}
		return newStr;
	}

	/**
	 * 验证邮箱地址是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		try {
			String regex = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(email);
			return matcher.matches();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		try {
			String regex = "^(1[3][0-9]{1}|1[5][0-9]{1}|1[8][0-9]{1}|1[7][0|6|7|8]|1[4][5|7])[0-9]{8}$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(mobile);
			return matcher.matches();
		} catch (Exception e) {
			return false;
		}
	}
}
