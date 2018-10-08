package com.android.component.net.parse.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description 网络请求返回解析对应JSON名
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-7-4 下午2:45:03
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONField {
	/**
	 * 对应的JSON解析名称,自身属性无此值默认属性名, 外键对象无此值默认外键对象属性值于自身属性在JSON中同级
	 * 
	 * @author 谢志杰
	 * @create 2015-8-4 下午2:54:47
	 * @return
	 */
	String name() default "";

	/**
	 * 是否是外键对象
	 * 
	 * @author 谢志杰
	 * @create 2015-8-4 下午2:54:29
	 * @return
	 */
	boolean isForeignObject() default false;

	/**
	 * 是否是外键对象列表
	 * 
	 * @author 谢志杰
	 * @create 2015-8-4 下午3:13:50
	 * @return
	 */
	boolean isForeignArray() default false;

	/**
	 * 外键对象Class
	 * 
	 * @author 谢志杰
	 * @create 2015-8-4 下午2:54:38
	 * @return
	 */
	Class<?> foreignClass() default Object.class;
}
