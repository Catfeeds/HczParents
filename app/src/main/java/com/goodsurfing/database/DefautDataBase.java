package com.goodsurfing.database;

/**
 * 聊天数据库
 * 
 * @author 周勇
 * @version 1.0 2014-7-4
 */
public class DefautDataBase {
	public static final int DB_VERSION = 1;

	public interface TableDefault {
		/**
		 * 表名
		 */
		String TABLE_NAME = "def_cache";

		public interface Column {

			/**
			 * 1.数据_ID
			 */
			String _ID = "_id";
			/**
			 * 头像id
			 */
			String ID = "id";
			/**
			 * 名字
			 */
			String NAME = "name";
			/**
			 * 手机号
			 */
			String PHONE = "phone";
			/**
			 * 用户ID
			 */
			String USERID="userid";
			/**
			 * 纬度
			 */
			String LONGITUDE = "longitude";
			/**
			 * 经度
			 */
			String LATITUDE = "latitude";
		}

		String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		/**
		 * 创建数据库sql
		 */
		// -------COLUMN---------TYPE----------------------//
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME//
				+ " ("//
				+ Column._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "// 1
				+ Column.ID + " LONG, "// 2
				+ Column.NAME + " TEXT, "// 3
				+ Column.PHONE + " TEXT, "// 4
				+ Column.USERID + " TEXT, "// 4
				+ Column.LONGITUDE + " DOUBLE, "// 5
				+ Column.LATITUDE + " DOUBLE "// 6
				+ ");";
	}
	
	public interface AppTable {
		/**
		 * 表名
		 */
		String TABLE_NAME = "app_cache";

		public interface Column {
			String _ID = "_id";
			String ID = "id";
			String NAME = "webtitle";
			String WEBSITE = "website";
			String WEBTYE="webtye";
			String WEBCREATETIME = "webcreatetime";
			String WEBSTATUS = "webstatus";
			String WEBCLASSTYPE = "webclasstype";
			String REASON = "reason";
			String ICON = "icon";
		}

		String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		/**
		 * 创建数据库sql
		 */
		// -------COLUMN---------TYPE----------------------//
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME//
				+ " ("//
				+ Column._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "// 1
				+ Column.ID + " TEXT, "// 2
				+ Column.NAME + " TEXT, "// 3
				+ Column.ICON + " TEXT, "// 4
				+ Column.REASON + " TEXT, "// 4
				+ Column.WEBCLASSTYPE + " TEXT, "// 5
				+ Column.WEBCREATETIME + " TEXT, "// 5
				+ Column.WEBTYE + " TEXT, "// 5
				+ Column.WEBSITE + " TEXT, "// 5
				+ Column.WEBSTATUS + " TEXT "// 6
				+ ");";
	}
}
