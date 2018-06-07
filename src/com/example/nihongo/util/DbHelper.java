package com.example.nihongo.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 操作下载列表dblist的数据库帮助类
 * 
 * @author administrator1
 * 
 */
public class DbHelper extends SQLiteOpenHelper {

	private static final String name = "dblist"; // 数据库名称
	private static final int version = 1; // 数据库版本

	public DbHelper(Context context) {
		// 第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
		this(context, name, null, version);

	}

	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS dblisttable (pk integer primary key,fileId integer,importance integer,lesson integer,num integer)");
//		db.execSQL("CREATE TABLE IF NOT EXISTS pack_ver_table (pk integer primary key,fileId integer,packageName varchar(255),version varchar(255))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
