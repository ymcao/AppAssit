package com.appassit.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.appassit.model.SunModel;

/**
 */
public class SunDataHelper extends BaseDataHelper {

	public SunDataHelper(Context context) {
		super(context);
	}

	@Override
	protected Uri getContentUri() {
		return DataProvider.WEATHER_CONTENT_URI;
	}

	private ContentValues getContentValues(SunModel model) {
		ContentValues values = new ContentValues();
		values.put(SunDBInfo.ID, model.id);
		values.put(SunDBInfo.JSON, model.toJson());
		return values;
	}

	public SunModel query(String id) {
		SunModel model = null;
		Cursor cursor = query(null, SunDBInfo.ID + " = ?", new String[] { id }, null);
		if (cursor.moveToFirst()) {
			model = SunModel.fromCursor(cursor);
		}
		cursor.close();
		return model;
	}

	public void insert(SunModel model) {
		insert(getContentValues(model));
	}

	public void bulkInsert(List<SunModel> models) {
		ArrayList<ContentValues> contentValueses = new ArrayList<ContentValues>();
		for (SunModel model : models) {
			ContentValues values = getContentValues(model);
			contentValueses.add(values);
		}
		ContentValues[] valueArray = new ContentValues[contentValueses.size()];
		bulkInsert(contentValueses.toArray(valueArray));
	}

	public int deleteAll() {
		synchronized (DataProvider.DBLock) {
			DBHelper mDbHelper = DataProvider.getDBHelper();
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			int row = db.delete(SunDBInfo.TABLE_NAME, null, null);
			return row;
		}
	}

	public static final class SunDBInfo implements BaseColumns {

		/** 表名 */
		public static final String TABLE_NAME = "assitweather";

		/** ID */
		public static final String ID = "id";

		/** JSON */
		public static final String JSON = "json";

		public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME).addColumn(ID, Column.DataType.INTEGER).addColumn(JSON, Column.DataType.TEXT);
	}
}
