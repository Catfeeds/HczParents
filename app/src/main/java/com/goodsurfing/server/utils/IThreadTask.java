package com.goodsurfing.server.utils;

/**
 * 子线程操作的接口
 *
 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
 * @author wangyang
 * @2014-2-24 上午10:08:55
 */
public interface IThreadTask {
	/**
	 * 子线程中执行的方法
	 * @author wangyang
	 * @2014-2-21 上午9:52:18
	 * @param object
	 * @param operationType
	 * @return
	 * @throws Exception
	 */
	Object doInBackground(Object object, int operationType) throws Exception;
	
	/**
	 * 主线程操作的方法
	 * @author wangyang
	 * @2014-2-21 上午9:52:44
	 * @param object
	 * @param operationType
	 * @param classType
	 */
	void doAfterTask(Object object, int operationType, int classType);
	
	/**
	 * 执行子线程前的方法
	 *
	 * @author wangyang
	 * 2014-4-11 下午5:55:04
	 */
	void onPreExecute();
}
