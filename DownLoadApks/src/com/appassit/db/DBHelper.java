package com.appassit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	protected static final String DB_NAME = "assitcity.db";
	protected static final int DB_VERSION = 2;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// WeatherDataHelper.WeatherDBInfo.TABLE.create(db);
		// WeatherCurDataHelper.WeatherCurDBInfo.TABLE.create(db);
		// WeatherTodayDataHelper.WeatherTodayDBInfo.TABLE.create(db);
		SunDataHelper.SunDBInfo.TABLE.create(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		SunDataHelper.SunDBInfo.TABLE.create(db);
	}
}
