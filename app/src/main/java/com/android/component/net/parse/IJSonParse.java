package com.android.component.net.parse;

import com.android.component.net.NetResult;


/**
 * @description JSON解析接口
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-7-4 下午2:45:03
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public interface IJSonParse {
	/**
	 * 自定义解析
	 * 
	 * @author 谢志杰
	 * @create 2015-7-7 下午4:17:03
	 * @param response
	 * @return 正确对象或JsonObject错误对象
	 */
	public Object parse(String response);

	/**
	 * 自动解析
	 * 
	 * @author 谢志杰
	 * @create 2015-7-7 下午4:17:29
	 * @param response
	 * @param clazz
	 * @return 正确对象或JsonObject错误对象
	 */
	public Object parse(String response, Class<?> objClass);

	/**
	 * 获取返回JSON对象
	 * 
	 * @author 谢志杰
	 * @create 2015-7-7 下午4:18:26
	 * @param result
	 * @return
	 */
	public NetResult parseJSON(String response);
}
