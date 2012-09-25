package org.aivan.androitest1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.method.DateTimeKeyListener;
import android.util.Log;

import static org.aivan.androitest1.HistoryDBOpenerHelper.*;

public class HistoryDAO {

	private static final String LOG_TAG = HistoryDAO.class.getName();

	Context context = null;
	HistoryDBOpenerHelper dbHelper = null;

	public HistoryDAO(Context context) {
		super();
		this.context = context;
		dbHelper = new HistoryDBOpenerHelper(context,
				HistoryDBOpenerHelper.DB_NAME, null, 2);
	}

	public void addHistoryRecord(long time, int level) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_DATE, time);
		values.put(COLUMN_VALUE, level);
		SQLiteDatabase histDB = dbHelper.getWritableDatabase();
		histDB.insert(DICTIONARY_TABLE_NAME, null, values);
		histDB.close();
	}

	/**
	 * Get Last "count" record from the DB as a simple string, usefull for
	 * debugging and monitoring
	 * 
	 * @param recordCount
	 *            How many last records to return in the result
	 * @return String with last "count" records, date and time properly
	 *         formatted as YYYY-MM-DD HH:MM:SS
	 */
	public String getLastRecords(int recordCount) {
		String result = "";

		// result = "Test one\ntest 2\n test3";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getDefault());

		// Log.d(LOG_TAG,"Default timezone is "+TimeZone.getDefault().getDisplayName());

		SQLiteDatabase histDB = dbHelper.getReadableDatabase();
		Cursor cursor = histDB.query(DICTIONARY_TABLE_NAME,
				HistoryDBOpenerHelper.columns, "", null, null, null,
				HistoryDBOpenerHelper.COLUMN_DATE + " desc", "" + recordCount);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					long date = cursor.getLong(cursor
							.getColumnIndex(COLUMN_DATE));
					String value = cursor.getString(cursor
							.getColumnIndex(COLUMN_VALUE));

					result += sdf.format(new Date(date)) + " : " + value + "\n";

				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		histDB.close();

		// long time = System.currentTimeMillis();
		// Log.d(LOG_TAG,time+"");
		// Log.d(LOG_TAG,sdf.format(new Date(time)));

		return result;
	}

	/**
	 * Task: 1. Delete "old" data (older then
	 * AndroBatConfiguration.MAX_DAYS_IN_HISTORY) 2. Remove duplicate records
	 * (same values) in range less then 12 hrs
	 */
	public void performDataCleanup() {
		Log.d(LOG_TAG, "performDataCleanup");

		// 1. Delete old records:
		SQLiteDatabase histDB = dbHelper.getWritableDatabase();
		int rowsDeleted = histDB.delete(
				DICTIONARY_TABLE_NAME,
				COLUMN_DATE
						+ " < ("
						+ (System.currentTimeMillis() - (AndroBatConfiguration.MAX_DAYS_IN_HISTORY * 24 * 60 * 60 * 1000))
						+ ")", null);
		Log.d(LOG_TAG, "Rows deleted :"+rowsDeleted);
		histDB.close();

	}

}
