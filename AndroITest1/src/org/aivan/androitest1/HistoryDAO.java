package org.aivan.androitest1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
		values.put(HISTORY_COLUMN_DATE, time);
		values.put(HISTORY_COLUMN_VALUE, level);
		SQLiteDatabase histDB = dbHelper.getWritableDatabase();
		histDB.insert(HISTORY_TABLE_NAME, null, values);
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
		Cursor cursor = histDB.query(HISTORY_TABLE_NAME,
				HistoryDBOpenerHelper.HISTORY_COLUMNS, "", null, null, null,
				HistoryDBOpenerHelper.HISTORY_COLUMN_DATE + " desc", "" + recordCount);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					long date = cursor.getLong(cursor
							.getColumnIndex(HISTORY_COLUMN_DATE));
					String value = cursor.getString(cursor
							.getColumnIndex(HISTORY_COLUMN_VALUE));

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
	 * (same values) in range less then MAX_MINUTES_PER_PERCENT minutes
	 */
	public void performDataCleanup() {
		Log.d(LOG_TAG, "performDataCleanup");

		// 1. Delete old records:
		SQLiteDatabase histDB = dbHelper.getWritableDatabase();
		int rowsDeleted = histDB.delete(
				HISTORY_TABLE_NAME,
				HISTORY_COLUMN_DATE
						+ " < ("
						+ (System.currentTimeMillis() - (AndroBatConfiguration.MAX_DAYS_IN_HISTORY * 24 * 60 * 60 * 1000))
						+ ")", null);
		Log.d(LOG_TAG, "Old records deleted :"+rowsDeleted);
		
		// TODO: COmplete date cleanup here (removing duplicates)
		
		Cursor c = histDB.query(HISTORY_TABLE_NAME, HISTORY_COLUMNS , null , null, null, null, HISTORY_COLUMN_DATE);
		
		long oldDate = 0;
		int oldValue = Integer.MIN_VALUE;
		if(c.moveToFirst()) {
			oldDate = c.getLong(c.getColumnIndex(HISTORY_COLUMN_DATE));
			oldValue = c.getInt(c.getColumnIndex(HISTORY_COLUMN_VALUE));
		}
		int duplicatesDeleted = 0;
		while(c.moveToNext()) {
			long newDate = c.getLong(c.getColumnIndex(HISTORY_COLUMN_DATE));
			int newValue = c.getInt(c.getColumnIndex(HISTORY_COLUMN_VALUE));
			if(oldValue == newValue && (newDate - oldDate) < (AndroBatConfiguration.MAX_MS_PER_PERCENT)) {
				// This is duplicate record that needs to be removed:
				histDB.delete(HISTORY_TABLE_NAME, HISTORY_COLUMN_DATE +" = " + newDate, null);
				duplicatesDeleted++;
			} else {
				// This record is OK, continue to the next one:
				oldDate = newDate;
				oldValue = newValue;
			}
		}
		
		Log.d(LOG_TAG, "Duplicate records deleted :"+duplicatesDeleted);
		
		histDB.close();

	}

}
