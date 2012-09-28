package org.aivan.androitest1.db;

import static org.aivan.androitest1.db.HistoryDBOpenerHelper.HISTORY_COLUMNS;
import static org.aivan.androitest1.db.HistoryDBOpenerHelper.HISTORY_COLUMN_DATE;
import static org.aivan.androitest1.db.HistoryDBOpenerHelper.HISTORY_COLUMN_VALUE;
import static org.aivan.androitest1.db.HistoryDBOpenerHelper.HISTORY_TABLE_NAME;
import static org.aivan.androitest1.db.HistoryDBOpenerHelper.STAT_COLUMN_AVERAGE;
import static org.aivan.androitest1.db.HistoryDBOpenerHelper.STAT_COLUMN_PERCENT;
import static org.aivan.androitest1.db.HistoryDBOpenerHelper.STAT_COLUMN_SAMPLE_COUNT;
import static org.aivan.androitest1.db.HistoryDBOpenerHelper.STAT_TABLE_NAME;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.aivan.androitest1.AndroBatConfiguration;
import org.aivan.androitest1.stats.StatisticsCalculator;
import org.aivan.androitest1.stats.StatisticsPercentageBasic;
import org.aivan.androitest1.stats.StatisticsPercentageBasic.StatRecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HistoryDAO {

	private static final String LOG_TAG = HistoryDAO.class.getName();

	Context context = null;
	HistoryDBOpenerHelper dbHelper = null;

	public HistoryDAO(Context context) {
		super();
		this.context = context;
		dbHelper = new HistoryDBOpenerHelper(context,
				HistoryDBOpenerHelper.DB_NAME, null,
				HistoryDBOpenerHelper.DB_VERSION);
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
				HistoryDBOpenerHelper.HISTORY_COLUMN_DATE + " desc", ""
						+ recordCount);
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
		int rowsDeleted = histDB
				.delete(HISTORY_TABLE_NAME,
						HISTORY_COLUMN_DATE
								+ " < ("
								+ (System.currentTimeMillis() - (AndroBatConfiguration.MAX_DAYS_IN_HISTORY * 24 * 60 * 60 * 1000))
								+ ")", null);
		Log.d(LOG_TAG, "Old records deleted :" + rowsDeleted);

		// TODO: COmplete date cleanup here (removing duplicates)

		Cursor c = histDB.query(HISTORY_TABLE_NAME, HISTORY_COLUMNS, null,
				null, null, null, HISTORY_COLUMN_DATE);

		long oldDate = 0;
		int oldValue = Integer.MIN_VALUE;
		if (c.moveToFirst()) {
			oldDate = c.getLong(c.getColumnIndex(HISTORY_COLUMN_DATE));
			oldValue = c.getInt(c.getColumnIndex(HISTORY_COLUMN_VALUE));
		}
		int duplicatesDeleted = 0;
		while (c.moveToNext()) {
			long newDate = c.getLong(c.getColumnIndex(HISTORY_COLUMN_DATE));
			int newValue = c.getInt(c.getColumnIndex(HISTORY_COLUMN_VALUE));
			if (oldValue == newValue
					&& (newDate - oldDate) < (AndroBatConfiguration.MAX_MS_PER_PERCENT)) {
				// This is duplicate record that needs to be removed:
				histDB.delete(HISTORY_TABLE_NAME, HISTORY_COLUMN_DATE + " = "
						+ newDate, null);
				duplicatesDeleted++;
			} else {
				// This record is OK, continue to the next one:
				oldDate = newDate;
				oldValue = newValue;
			}
		}

		Log.d(LOG_TAG, "Duplicate records deleted :" + duplicatesDeleted);

		histDB.close();

	}

	/**
	 * Iterate all records calling StatisticsCalculator.evaluate for each pair
	 * (old/new)
	 * 
	 * @param stats
	 *            StatisticsCalculator
	 */
	public void iterateRecords(StatisticsCalculator stats) {
		SQLiteDatabase histDB = dbHelper.getReadableDatabase();

		Cursor cursor = histDB.query(HISTORY_TABLE_NAME,
				HistoryDBOpenerHelper.HISTORY_COLUMNS, "", null, null, null,
				HistoryDBOpenerHelper.HISTORY_COLUMN_DATE + " asc");

		if (cursor.moveToFirst()) {
			long oldDate = cursor.getLong(cursor
					.getColumnIndex(HISTORY_COLUMN_DATE));
			int oldValue = cursor.getInt(cursor
					.getColumnIndex(HISTORY_COLUMN_VALUE));
			while (cursor.moveToNext()) {
				long newDate = cursor.getLong(cursor
						.getColumnIndex(HISTORY_COLUMN_DATE));
				int newValue = cursor.getInt(cursor
						.getColumnIndex(HISTORY_COLUMN_VALUE));
				stats.evaluate(oldDate, oldValue, newDate, newValue);
				oldDate = newDate;
				oldValue = newValue;
			}
		}
		cursor.close();
		histDB.close();

	}

	public void storeStats(StatRecord[] statRecords) {
		SQLiteDatabase histDB = dbHelper.getWritableDatabase();
		histDB.delete(STAT_TABLE_NAME, "1", null);

		int counter = 0;
		for (StatRecord rec : statRecords) {

			ContentValues values = new ContentValues();
			values.put(STAT_COLUMN_PERCENT, counter);
			values.put(STAT_COLUMN_SAMPLE_COUNT, rec.sampleCount);
			values.put(STAT_COLUMN_AVERAGE, rec.average);

			histDB.insert(STAT_TABLE_NAME, null, values);

			counter++;
		}

		histDB.close();
	}

	public StatRecord[] loadStats(StatisticsPercentageBasic statistics) {
		ArrayList<StatRecord> stats = new ArrayList<StatRecord>();

		SQLiteDatabase histDB = dbHelper.getReadableDatabase();

		Cursor cursor = histDB.query(STAT_TABLE_NAME,
				HistoryDBOpenerHelper.STAT_COLUMNS, "", null, null, null,
				HistoryDBOpenerHelper.STAT_COLUMN_PERCENT + " asc");

		for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor
				.moveToNext()) {

			StatRecord rec = statistics.new StatRecord();
			rec.sampleCount = cursor.getInt(cursor
					.getColumnIndex(STAT_COLUMN_SAMPLE_COUNT));
			rec.average = cursor.getLong(cursor
					.getColumnIndex(STAT_COLUMN_AVERAGE));
			stats.add(rec);
		}

		return stats.toArray(new StatRecord[] {});
	}

	public int getLastBatteryLevel() {
		
		int result = Integer.MIN_VALUE;
		
		SQLiteDatabase histDB = dbHelper.getReadableDatabase();
		Cursor cursor = histDB.query(HISTORY_TABLE_NAME,
				HistoryDBOpenerHelper.HISTORY_COLUMNS, "", null, null, null,
				HistoryDBOpenerHelper.HISTORY_COLUMN_DATE + " desc", "1");
		if (cursor != null && cursor.moveToFirst()) {
			result = cursor.getInt(cursor
					.getColumnIndex(HISTORY_COLUMN_VALUE));

			cursor.close();
		}
		histDB.close();
		return result;
	}

	public long getLastBatteryDate() {
		
		long result = Long.MIN_VALUE;
		
		SQLiteDatabase histDB = dbHelper.getReadableDatabase();
		Cursor cursor = histDB.query(HISTORY_TABLE_NAME,
				HistoryDBOpenerHelper.HISTORY_COLUMNS, "", null, null, null,
				HistoryDBOpenerHelper.HISTORY_COLUMN_DATE + " desc", "1");
		if (cursor != null && cursor.moveToFirst()) {
			result = cursor.getLong(cursor
					.getColumnIndex(HISTORY_COLUMN_DATE));

			cursor.close();
		}
		histDB.close();
		return result;
	}

	public void updateLastHistoryRecordTime(long time) {
		long lastTime = getLastBatteryDate();
		
		SQLiteDatabase histDB = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(HISTORY_COLUMN_DATE, time);
		
		histDB.update(HISTORY_TABLE_NAME, values, HISTORY_COLUMN_DATE + " = "+lastTime, null);
		histDB.close();
		
	}

}
