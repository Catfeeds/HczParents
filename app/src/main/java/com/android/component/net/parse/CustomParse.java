package com.android.component.net.parse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.component.net.NetResult;
import com.android.component.net.parse.annotation.JSONField;
import com.android.component.utils.StringUtil;

/**
 * @description 自定义JSON解析方式
 * 
 * @author 谢志杰 E-mail: xzj2125123@163.com
 * @create 2015-7-7 下午3:00:40
 * @version 1.0.0
 * @company 湖南一石科技有限公司 Copyright: 版权所有 (c) 2016
 */
public class CustomParse extends BaseJSonParse {

	@Override
	public Object parse(String response, Class<?> objClass) {
		Object obj = null;
		NetResult jsonObj = parseJSON(response);
		if (jsonObj.getState() != JSONKey.STATE_SUCCESS) {
			return jsonObj;
		}
		try {
			if (jsonObj.getData().startsWith("[")) {
				obj = parseArray(jsonObj.getData(), objClass);
			} else {
				obj = parseObject(jsonObj.getData(), objClass);
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			jsonObj.setErrorMsg("数据格式错误");
		}
		return jsonObj;
	}

	/**
	 * 解析JSON返回对象列表或JsonObject
	 * 
	 * @author 谢志杰
	 * @create 2015-7-7 下午4:25:07
	 * @param response
	 * @param objClass
	 * @return
	 * @throws JSONException
	 */
	public Object parseArray(String message, Class<?> objClass)
			throws JSONException {
		if (StringUtil.isEmpty(message)) {
			NetResult jsonObj = new NetResult();
			jsonObj.setState(JSONKey.STATE_SUCCESS);
			jsonObj.setErrorMsg("无数据");
			return jsonObj;
		}
		List<Object> objList = new ArrayList<Object>();
		JSONArray jsonArray = new JSONArray(message);
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject msgObj = jsonArray.getJSONObject(i);
				Object obj = getObject(msgObj, objClass);
				objList.add(obj);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return objList;
	}

	/**
	 * 解析JSON返回对象或JsonObject
	 * 
	 * @author 谢志杰
	 * @create 2015-7-7 下午4:25:07
	 * @param response
	 * @param objClass
	 * @return
	 * @throws Exception
	 */
	public Object parseObject(String message, Class<?> objClass)
			throws Exception {
		if (StringUtil.isEmpty(message)) {
			NetResult jsonObj = new NetResult();
			jsonObj.setState(JSONKey.STATE_SUCCESS);
			jsonObj.setErrorMsg("无数据");
			return jsonObj;
		}
		JSONObject msgObj = new JSONObject(message);
		return getObject(msgObj, objClass);
	}

	/**
	 * 生成解析对象
	 * 
	 * @author 谢志杰
	 * @create 2015-7-7 下午3:33:19
	 * @param msgObj
	 * @param objClass
	 * @return
	 * @throws Exception
	 */
	public Object getObject(JSONObject msgObj, Class<?> objClass)
			throws Exception {
		if (msgObj == null || objClass == null)
			throw new Exception("msgObj or objClass is Null!");
		Object obj = objClass.newInstance();
		Field[] fields = objClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			// 去掉static字段
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			try {
				field.set(obj, getFieldValue(field, msgObj));
			} catch (Exception e) {
				continue;
			}
		}
		return obj;
	}

	/**
	 * 获取Field在返回json中的属性名
	 * 
	 * @author 谢志杰
	 * @create 2015-7-4 下午5:27:24
	 * @param field
	 * @return
	 */
	private String getFieldName(Field field) {
		String fieldName = "";
		JSONField json = field.getAnnotation(JSONField.class);
		if (json != null)
			fieldName = json.name();
		if (StringUtil.isEmpty(fieldName)) {
			fieldName = field.getName();
		}
		return fieldName;
	}

	/**
	 * 获取Field在返回json中的值
	 * 
	 * @author 谢志杰
	 * @create 2015-7-4 下午5:32:03
	 * @param field
	 * @param msgObj
	 * @return
	 * @throws JSONException
	 */
	private Object getFieldValue(Field field, JSONObject msgObj)
			throws JSONException {
		field.setAccessible(true);
		Object fieldValue = null;
		boolean isForeignObject = isForeignObject(field);
		boolean isForeignArray = isForeignArray(field);
		if (isForeignObject || isForeignArray) {
			try {
				Class<?> foreignClass = getForeignClass(field);
				Object obj = null;
				if (isForeignObject)
					obj = parseObject(getForeignJson(field, msgObj),
							foreignClass);
				else if (isForeignArray)
					obj = parseArray(getForeignJson(field, msgObj),
							foreignClass);
				if (obj instanceof NetResult) {
					fieldValue = null;
				} else {
					fieldValue = obj;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Class<?> responseType = field.getType();
			String fieldName = getFieldName(field);
			if ("int".equals(responseType.toString().trim())
					|| Integer.class == responseType) {
				fieldValue = msgObj.getInt(fieldName);
			} else if ("float".equals(responseType.toString().trim())
					|| Float.class == responseType) {
				fieldValue = (float) msgObj.getDouble(fieldName);
			} else if ("double".equals(responseType.toString().trim())
					|| Double.class == responseType) {
				fieldValue = msgObj.getDouble(fieldName);
			} else if ("long".equals(responseType.toString().trim())
					|| Long.class == responseType) {
				fieldValue = msgObj.getLong(fieldName);
			} else if ("boolean".equals(responseType.toString().trim())
					|| Boolean.class == responseType) {
				fieldValue = msgObj.getBoolean(fieldName);
			} else if (responseType == String.class) {
				fieldValue = msgObj.getString(fieldName);
			}
		}
		return fieldValue;
	}

	/**
	 * 判断是否是外键对象
	 * 
	 * @author 谢志杰
	 * @create 2015-8-4 下午2:56:13
	 * @param field
	 * @return
	 */
	private boolean isForeignObject(Field field) {
		JSONField json = field.getAnnotation(JSONField.class);
		if (json != null) {
			return json.isForeignObject();
		}
		return false;
	}

	/**
	 * 判断是否是外键对象列表
	 * 
	 * @author 谢志杰
	 * @create 2015-8-4 下午2:56:13
	 * @param field
	 * @return
	 */
	private boolean isForeignArray(Field field) {
		JSONField json = field.getAnnotation(JSONField.class);
		if (json != null) {
			return json.isForeignArray();
		}
		return false;
	}

	/**
	 * 获取外键对象Class
	 * 
	 * @author 谢志杰
	 * @create 2015-8-4 下午2:57:59
	 * @param field
	 * @return
	 */
	private Class<?> getForeignClass(Field field) {
		JSONField json = field.getAnnotation(JSONField.class);
		if (json != null) {
			return json.foreignClass();
		}
		return null;
	}

	/**
	 * 获取外键解析对象JSON
	 * 
	 * @author 谢志杰
	 * @create 2015-8-4 下午3:09:19
	 * @param field
	 * @return
	 * @throws JSONException
	 */
	private String getForeignJson(Field field, JSONObject msgObj)
			throws JSONException {
		JSONField json = field.getAnnotation(JSONField.class);
		if (json != null) {
			String name = json.name();
			if (StringUtil.isEmpty(name))
				return msgObj.toString();
			return msgObj.getString(name);
		}
		return null;
	}
}
