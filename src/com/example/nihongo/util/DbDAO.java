package com.example.nihongo.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.nihongo.util.Constance;

/**
 * 对下载列表dblist的dao封装
 * 
 * @author administrator1
 * 
 */
public class DbDAO {
	private DbHelper mDbHelper;

	private DbDAO(Context context) {
		mDbHelper = new DbHelper(context);
	}

	private static DbDAO intance = null;// 静态私用成员，没有初始化

	public static synchronized DbDAO getInstance(Context context) // 静态，同步，公开访问点
	{
		if (intance == null) {
			intance = new DbDAO(context);
		}
		return intance;
	}

	public synchronized void addOrUpdateWord(int fileId, int importance, int lesson,
			int num) {
		deleteRowFromPackVerTable(fileId);
		addWord( fileId,  importance,  lesson,
				 num);
	}
	
	private synchronized void deleteRowFromPackVerTable(int fileId) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete("dblisttable", "fileId=?", new String[] { "" + fileId });
		db.close();
	}

	private synchronized long addWord(int fileId, int importance, int lesson,
			int num) {
		SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("fileId", fileId);
		contentValues.put("importance", importance);
		contentValues.put("lesson", lesson);
		contentValues.put("num", num);
		long rowid = sqLiteDatabase.insert("dblisttable", null, contentValues);
		sqLiteDatabase.close();// 必须和所有方法同步
		return rowid;
	}

	public synchronized void plusImportance(int lesson, int num){
		int importance  = queryImportance(lesson, num);
		int newImportance = importance+Constance.plusPace;
		if(newImportance >= Constance.maxImportance){
			newImportance = Constance.maxImportance;
		}
		updateWord(newImportance,lesson,num);
	}
	
	public synchronized void minusImportance(int lesson,int num){
		int importance  = queryImportance(lesson, num);
		int newImportance = importance -Constance.minusPace;
		if(newImportance <= Constance.minImportance){
			newImportance = Constance.minImportance;
		}
		updateWord(newImportance,lesson,num);
	}
	
	private synchronized int updateWord(int importance, int lesson, int num) {
		SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("importance", importance);
		int rowsAffected = sqLiteDatabase.update("dblisttable", values,
				"lesson = ? and num = ?",
				new String[] { "" + lesson, "" + num });
		sqLiteDatabase.close();
		return rowsAffected;
	}

	/**
	 * 从dblisttable中，查询id符合的行
	 * 
	 * @param fileId
	 * @return 如果是返回-1,那表明没有查找到,正常应该返回[0,10]
	 */
	public synchronized int queryImportance(int fileId) {
		SQLiteDatabase readableDatabase = null;
		Cursor cursor = null;
		try {
			readableDatabase = mDbHelper.getReadableDatabase();

			cursor = readableDatabase.query("dblisttable",
					new String[] { "importance" }, "fileId=?",
					new String[] { "" + fileId }, null, null, null);

			if (cursor == null) {
				return -1;
			} else {
				if (cursor.moveToNext()) {
					int importance = cursor.getInt(cursor
							.getColumnIndex("importance"));
					return importance;
				} else {
					return -1;
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (readableDatabase != null) {
				readableDatabase.close();
			}
		}
	}
	
	/**
	 * 从dblisttable中，查询id符合的行
	 * 
	 * @param fileId
	 * @return 如果是返回-1,那表明没有查找到,正常应该返回[0,10]
	 */
	public synchronized int queryImportance(int lessonNum,int num) {
		SQLiteDatabase readableDatabase = null;
		Cursor cursor = null;
		try {
			readableDatabase = mDbHelper.getReadableDatabase();

			cursor = readableDatabase.query("dblisttable",
					new String[] { "importance" }, "lesson = ? and num = ?",
					new String[] { "" + lessonNum, "" + num }, null, null, null);
			if (cursor == null) {
				return -1;
			} else {
				if (cursor.moveToNext()) {
					int importance = cursor.getInt(cursor
							.getColumnIndex("importance"));
					return importance;
				} else {
					return -1;
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (readableDatabase != null) {
				readableDatabase.close();
			}
		}
	}

}
