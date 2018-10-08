package com.goodsurfing.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.goodsurfing.beans.Friend;
import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.DefDbHelper;
import com.goodsurfing.database.DefautDataBase.TableDefault;
import com.goodsurfing.database.DefautDataBase.TableDefault.Column;

/**
 */
public class FriendDao {
	private SQLiteDatabase db;

	public FriendDao(Context context) {
		db = DefDbHelper.getDBInstance(context);
	}

	public void insert(List<Friend> Friends) {
		for (Friend friend : Friends) {
			if (selectCircle(friend.getPhone()) > 0) {
				continue;
			}
			insertFriend(friend);
		}
	}

	public long selectCircle(String phone) {
		long _id = -1;
		Cursor cur=db.query( TableDefault.TABLE_NAME, null, TableDefault.Column.PHONE+" = ?", new String[]{phone}, null, null, null);
		
		
		if (cur.getCount() > 0)
			_id = 1;
		if (null != cur) {
			cur.close();
			cur = null;
		}
		return _id;
	}

	/**
	 * 根据手机号 更改昵称
	 * 
	 * @author xzj
	 * @2014-11-17 上午11:59:57
	 * @param did
	 * @param status
	 * @return
	 */
	public int upData4Name(String phone, String name) {
		ContentValues values = new ContentValues();
		values.put(TableDefault.Column.NAME, name);
		return db.update(TableDefault.TABLE_NAME, values, TableDefault.Column.PHONE + " = ? ", new String[] { phone });
	}

	/**
	 * 根据手机号 更改头像
	 * 
	 * @author xzj
	 * @2014-11-17 上午11:59:57
	 * @param did
	 * @param status
	 * @return
	 */
	public int upData4head(String phone, int id) {
		ContentValues values = new ContentValues();
		values.put(TableDefault.Column.ID, id);
		return db.update(TableDefault.TABLE_NAME, values, TableDefault.Column.PHONE + " = ? ", new String[] { phone });
	}

	/**
	 * 插入 说说主体
	 * 
	 * @author xzj
	 * @2014-11-14 上午11:47:36
	 * @param pageindex
	 * @param Friend
	 * @return
	 */
	public long insertFriend(Friend friend) {
		long _id = -1;
		ContentValues values = new ContentValues();
		values.put(TableDefault.Column._ID, friend.get_id());
		values.put(TableDefault.Column.ID, friend.getId());
		values.put(TableDefault.Column.NAME, friend.getNikename());
		values.put(TableDefault.Column.LATITUDE, friend.getLatitude());
		values.put(TableDefault.Column.LONGITUDE, friend.getLongitude());
		values.put(TableDefault.Column.PHONE, friend.getPhone());
		values.put(TableDefault.Column.USERID, Constants.userId);
		_id = db.insert(TableDefault.TABLE_NAME, null, values);
		return _id;
	}

	public List<Friend> getFriendList() {

		List<Friend> list = new ArrayList<Friend>();
		try {
			Cursor cur=db.query( TableDefault.TABLE_NAME, null, TableDefault.Column.USERID+" = ?", new String[]{ Constants.userId}, null, null, null);
			while (cur.moveToNext()) {
				Friend c = new Friend();
				c.set_id(cur.getInt(cur.getColumnIndex(Column._ID)));
				c.setId(cur.getInt(cur.getColumnIndex(Column.ID)));
				c.setNikename(cur.getString(cur.getColumnIndex(Column.NAME)));
				c.setLatitude(cur.getDouble(cur.getColumnIndex(Column.LATITUDE)));
				c.setLongitude(cur.getDouble(cur.getColumnIndex(Column.LONGITUDE)));
				c.setPhone(cur.getString(cur.getColumnIndex(Column.PHONE)));
				list.add(c);
			}
			if (null != cur) {
				cur.close();
				cur = null;
			}
			return list;
		} catch (Exception e) {
			return list;
		}
	}

	/**
	 * 根据手机号删除 孩子
	 * 
	 * @author xzj
	 * @2014-11-14 下午2:30:26
	 * @param did
	 * @return
	 */
	public int deleteFriend(String phone) {
		try {
			int row = db.delete(TableDefault.TABLE_NAME, Column.PHONE + " = ? ", new String[] { phone });
			return row;
		} catch (Exception e) {
			return -1;
		}
	}

}
