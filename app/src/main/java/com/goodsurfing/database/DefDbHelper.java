package com.goodsurfing.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.goodsurfing.constants.Constants;
import com.goodsurfing.database.DefautDataBase.AppTable;
import com.goodsurfing.database.DefautDataBase.TableDefault;

/**
 * 
 * @author 谢志杰
 * @version 1.0 2014-7-4
 */
public class DefDbHelper extends SQLiteOpenHelper {

	private static DefDbHelper instance;
	private static SQLiteDatabase db;

	private static synchronized DefDbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DefDbHelper(context.getApplicationContext());
		}
		return instance;
	}

	public static synchronized SQLiteDatabase getDBInstance(Context context) {
		if (db == null || !db.isOpen()) {
			db = getInstance(context).getWritableDatabase();
		}
		return db;
	}

	public static void closeDB() {
		if (db != null) {
			db.close();
		}
	}

	public static void reStart() {
		instance = null;
		db = null;

	}

	private DefDbHelper(Context context) {
		this(context, null, null, 0);
	}

	private DefDbHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, Constants.DB_NAME_CACHE, factory, DefautDataBase.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TableDefault.CREATE_TABLE);
		db.execSQL(AppTable.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(TableDefault.DROP_TABLE);
		db.execSQL(TableDefault.CREATE_TABLE);
		db.execSQL(AppTable.DROP_TABLE);
		db.execSQL(AppTable.CREATE_TABLE);
	}

}
