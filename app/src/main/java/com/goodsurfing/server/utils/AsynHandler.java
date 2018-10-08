package com.goodsurfing.server.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 自定义Handler,继承自Handler并实现了ITask接口,并且内部维护了一个IThreadTask接口变量,
 * 这样既可以传入一个IThreadTask接口,也可以实现自己内部的放,使用更灵活
 * 
 * Copyright: 版权所有 (c) 2014 Company: 北京开拓明天科技有限公司
 * 
 * @author zhouyong
 * @2014-2-24 上午10:38:07
 */
public abstract class AsynHandler extends Handler implements IThreadTask {

	/** 错误返回码 */
	public static final int ERROR = -100;
	/** 成功返回码 */
	public static final int SUCCESS = -200;

	/** 线程池 */
	private static ExecutorService executors = Executors.newCachedThreadPool();

	/** IThreadTask接口变量 */
	protected IThreadTask task = null;

	/** 上下文对象 */
	protected Context context;

	/** 刷新主线程模式 */
	protected Mode refreshMode = Mode.SEND_MESSAGE;

	public AsynHandler(Context context, IThreadTask task) {
		this.context = context;
		this.task = task;
	}

	public AsynHandler(Context context) {
		super();
		this.context = context;
	}

	public void setRefreshMode(Mode refreshMode) {
		this.refreshMode = refreshMode;
	}

	/**
	 * 更新主线程的方式,默认为SEND_MESSAGE
	 * 
	 * @author zhouyong
	 * @2014-2-21 上午11:31:59
	 */
	public enum Mode {
		/**
		 * 发送消息
		 */
		SEND_MESSAGE,
		/**
		 * 发送广播
		 */
		SEND_RECEIVER,
		/**
		 * 发送消息和广播
		 */
		BOTH
	}

	/**
	 * 得到Message后对Message的操作
	 * 
	 * @author zhouyong
	 * @2014-2-21 上午9:49:15
	 * @param msg
	 */
	@Override
	public void handleMessage(Message msg) {
		if (task == null)
			doAfterTask(msg.obj, msg.what, msg.arg1);
		else
			task.doAfterTask(msg.obj, msg.what, msg.arg1);
	}

	/**
	 * 执行子线程前的方法
	 * 
	 * @author zhouyong 2014-4-11 下午5:53:45
	 */
	@Override
	public void onPreExecute() {
		throw new RuntimeException("请在子类重写该方法!!!");
	}

	/**
	 * 通过传入路径来实现后台下载
	 * 
	 * @auther zhouyong
	 * @param path
	 * @return
	 */
	@Override
	public Object doInBackground(Object object, int operationType) throws Exception {
		throw new RuntimeException("请在子类重写该方法!!!");
	};

	/**
	 * 消息处理机制调用的执行任务方法,用于对得到消息队列的Message的处理
	 * 
	 * @auther zhouyong
	 * @param t
	 * @param operationType
	 */
	@Override
	public void doAfterTask(Object t, int operationType, int classType) {
		throw new RuntimeException("请在子类重写该方法!!!");
	};

	/**
	 * 通过子线程下载及调用消息队列处理机制,先调用doInBackground进行后台下载, 一旦下载结束调用消息处理机制传递消息。
	 * 
	 * @author zhouyong
	 * @2014-3-3 下午4:56:13
	 * @param operationType
	 * @param classType
	 */
	public void startThread(final int operationType, final int classType) {
		startThread(null, operationType, classType);
	}

	/**
	 * 通过子线程下载及调用消息队列处理机制,先调用doInBackground进行后台下载, 一旦下载结束调用消息处理机制传递消息。
	 * 
	 * @author zhouyong
	 * @2014-3-3 下午4:56:13
	 * @param operationType
	 */
	public void startThread(final int operationType) {
		startThread(null, operationType, operationType);
	}

	/**
	 * 通过子线程下载及调用消息队列处理机制,先调用doInBackground进行后台下载, 一旦下载结束调用消息处理机制传递消息。
	 * 
	 * @author zhouyong
	 * @2014-3-3 下午4:56:13
	 * @param operationType
	 */
	public void startThread(final Object object, final int operationType) {
		startThread(object, operationType, operationType);
	}

	/**
	 * 通过子线程下载及调用消息队列处理机制,先调用doInBackground进行后台下载, 一旦下载结束调用消息处理机制传递消息。
	 * 
	 * @author zhouyong
	 * @2014-3-3 下午4:56:13
	 */
	public void startThreadAndSendEmptyMessage() {
		startThread(null, 0, 0);
	}

	/**
	 * 通过子线程下载及调用消息队列处理机制,先调用doInBackground进行后台下载, 一旦下载结束调用消息处理机制传递消息。
	 * 
	 * @author zhouyong
	 * @2014-3-3 下午4:56:13
	 * @param object
	 * @param operationType
	 * @param classType
	 */
	public void startThread(final Object object, final int operationType, final int classType) {
		if (task == null)
			onPreExecute();
		else
			task.onPreExecute();

		executors.execute(new Runnable() {
			@Override
			public void run() {
				Message msg = Message.obtain();
				try {
					// 判断IThreadTask接口变量,并执行后台执行方法得到返回值
					if (task == null)
						msg.obj = doInBackground(object, operationType);
					else
						msg.obj = task.doInBackground(object, operationType);

					// 设置消息的int变量
					msg.what = operationType;
					msg.arg1 = classType;

					// 根据刷新模式发送消息或者广播
					switch (refreshMode) {
					case SEND_MESSAGE:
						sendMessage(msg);
						break;
					case SEND_RECEIVER:
						break;
					case BOTH:
						sendMessage(msg);
						break;
					}
				} catch (Exception e) {
					msg.obj = null;
					msg.what = ERROR;
					sendMessage(msg);
					e.printStackTrace();
				}
			}
		});
	}

	public void sendMessage(Object obj, int what) {
		Message msg = Message.obtain();
		msg.what = what;
		if (obj != null)
			msg.obj = obj;
		sendMessage(msg);
	}
}
