package com.android.component.net.parse;

import com.android.component.net.NetResult;

/**
 * @description JSON自动解析工具类
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-7-8 上午10:23:31
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class JSONParse {

	private IJSonParse mParse;

	private ParseType mParseType;

	public JSONParse() {
		mParseType = ParseType.fastJsonParse;
	}

	public JSONParse(ParseType parseType) {
		this.mParseType = parseType;
	}

	public void setParseType(ParseType parseType) {
		this.mParseType = parseType;
	}

	public Object parse(String response, Class<?> objClass) {
		mParse = mParseType.getJSonParse();
		return mParse.parse(response, objClass);
	}

	public NetResult parseJSON(String response) {
		mParse = mParseType.getJSonParse();
		return mParse.parseJSON(response);
	}

	public enum ParseType {
		fastJsonParse, gsonParse, customParse;

		public IJSonParse getJSonParse() {
			IJSonParse parse = null;
			switch (this) {
			case fastJsonParse:
				parse = new FastJSonParse();
				break;
			case gsonParse:
				parse = new GsonParse();
				break;
			case customParse:
				parse = new CustomParse();
				break;
			}
			return parse;
		}
	}
}
