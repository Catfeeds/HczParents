package com.goodsurfing.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.component.utils.Log;
import com.goodsurfing.beans.WebFilterBean;
import com.goodsurfing.database.DefDbHelper;
import com.goodsurfing.database.DefautDataBase.AppTable;
import com.goodsurfing.database.DefautDataBase.AppTable.Column;

/**
 */
public class AppDao {
	private SQLiteDatabase db;

	public AppDao(Context context) {
		db = DefDbHelper.getDBInstance(context);
	}

	public void insert(List<WebFilterBean> beans) {
		for (WebFilterBean bean : beans) {
			if (selectWebFilterBean(bean.getId()) > 0) {
				continue;
			}
			insertWebFilterBean(bean);
		}
	}

	public long selectWebFilterBean(String id) {
		long _id = -1;
		Cursor cur=db.query( AppTable.TABLE_NAME, null, AppTable.Column.ID+"=?", new String[]{id}, null, null, null);
		if (cur.getCount() > 0)
			_id = 1;
		if (null != cur) {
			cur.close();
			cur = null;
		}
		return _id;
	}

	/**
	 * 根据ID 更改APP
	 * 
	 * @author xzj
	 * @2014-11-17 上午11:59:57
	 * @param did
	 * @param status
	 * @return
	 */
	public int upData4head(String id, String status) {
		ContentValues values = new ContentValues();
		values.put(AppTable.Column.WEBSTATUS, status);
		return db.update(AppTable.TABLE_NAME, values, Column.ID + " = ? ", new String[] { id });
	}

	/**
	 * 插入APP
	 * 
	 * @author xzj
	 * @2014-11-14 上午11:47:36
	 * @param pageindex
	 * @param WebFilterBean
	 * @return
	 */
	public long insertWebFilterBean(WebFilterBean WebFilterBean) {
		long _id = -1;
		ContentValues values = new ContentValues();
		values.put(AppTable.Column._ID, WebFilterBean.get_id());
		values.put(AppTable.Column.ID, WebFilterBean.getId());
		values.put(AppTable.Column.NAME, WebFilterBean.getWebTitle());
		values.put(AppTable.Column.REASON, WebFilterBean.getReason());
		values.put(AppTable.Column.WEBCLASSTYPE, WebFilterBean.getWebClassType());
		values.put(AppTable.Column.WEBCREATETIME, WebFilterBean.getWebCreateTime());
		values.put(AppTable.Column.WEBSITE, WebFilterBean.getWebSite());
		values.put(AppTable.Column.WEBSTATUS, WebFilterBean.getWebStatus());
		values.put(AppTable.Column.WEBTYE, WebFilterBean.getWebTye());
		values.put(AppTable.Column.ICON, WebFilterBean.getIcon());
		_id = db.insert(AppTable.TABLE_NAME, null, values);
		return _id;
	}

	public List<WebFilterBean> getWebFilterBeanList() {

		List<WebFilterBean> list = new ArrayList<WebFilterBean>();
		try {
			String sql = "";
			Cursor cur=db.query( AppTable.TABLE_NAME, null,null,null, null, null, null);
			while (cur.moveToNext()) {
				WebFilterBean c = new WebFilterBean();
				c.setId(cur.getString(cur.getColumnIndex(Column.ID)));
				c.setIcon(cur.getString(cur.getColumnIndex(Column.ICON)));
				c.setReason(cur.getString(cur.getColumnIndex(Column.REASON)));
				c.setWebClassType(cur.getString(cur.getColumnIndex(Column.WEBCLASSTYPE)));
				c.setWebCreateTime(cur.getString(cur.getColumnIndex(Column.WEBCREATETIME)));
				c.setWebSite(cur.getString(cur.getColumnIndex(Column.WEBSITE)));
				c.setWebStatus(cur.getString(cur.getColumnIndex(Column.WEBSTATUS)));
				c.setWebTye(cur.getString(cur.getColumnIndex(Column.WEBTYE)));
				c.setWebTitle(cur.getString(cur.getColumnIndex(Column.NAME)));
				c.setSelected(false);
				list.add(c);
			}
			if (null != cur) {
				cur.close();
				cur = null;
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	/**
	 * 根据 名字搜索APP
	 * 
	 * @param name
	 * @return
	 */
	public List<WebFilterBean> getWebFilterBeanList4Name(String name) {

		List<WebFilterBean> list = new ArrayList<WebFilterBean>();
		try {
			Cursor cur=db.query( AppTable.TABLE_NAME, null, AppTable.Column.NAME+"=?", new String[]{"'%" + name + "%'"}, null, null, null);
			while (cur.moveToNext()) {
				WebFilterBean c = new WebFilterBean();
				c.setId(cur.getString(cur.getColumnIndex(Column.ID)));
				c.setIcon(cur.getString(cur.getColumnIndex(Column.ICON)));
				c.setReason(cur.getString(cur.getColumnIndex(Column.REASON)));
				c.setWebClassType(cur.getString(cur.getColumnIndex(Column.WEBCLASSTYPE)));
				c.setWebCreateTime(cur.getString(cur.getColumnIndex(Column.WEBCREATETIME)));
				c.setWebSite(cur.getString(cur.getColumnIndex(Column.WEBSITE)));
				c.setWebStatus(cur.getString(cur.getColumnIndex(Column.WEBSTATUS)));
				c.setWebTye(cur.getString(cur.getColumnIndex(Column.WEBTYE)));
				c.setWebTitle(cur.getString(cur.getColumnIndex(Column.NAME)));
				c.setSelected(false);
				list.add(c);
			}
			if (null != cur) {
				cur.close();
				cur = null;
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	/**
	 * 根据id删除 APP
	 * 
	 * @author xzj
	 * @2014-11-14 下午2:30:26
	 * @param did
	 * @return
	 */
	public int deleteWebFilterBean(String id) {
		try {
			int row = db.delete(AppTable.TABLE_NAME, Column.ID + " = ? ", new String[] { id });
			return row;
		} catch (Exception e) {
			return -1;
		}
	}

	// update 表 set 字段=null
	/**
	 * 
	 * @author xzj
	 * @2014-11-14 下午2:30:26
	 * @param did
	 * @return
	 */
	public int clearWebFilterBean() {
		try {
			ContentValues values = new ContentValues();
			values.put(Column.WEBSTATUS, "2");
			return db.update(AppTable.TABLE_NAME, values, null, null);
		} catch (Exception e) {
			return -1;
		}
	}

	public void deleteAllWebFilterBean() {
		try {
			int row = db.delete(AppTable.TABLE_NAME, null, null);
		} catch (Exception e) {
		}
	}

}
