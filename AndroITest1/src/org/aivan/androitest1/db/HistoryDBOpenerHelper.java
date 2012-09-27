package org.aivan.androitest1.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDBOpenerHelper extends SQLiteOpenHelper {

	static final int DB_VERSION = 3;

	static final String DB_NAME = "historyDB";

	public static final String HISTORY_COLUMN_DATE = "date";
	public static final String HISTORY_COLUMN_VALUE = "value";

	public static final String[] HISTORY_COLUMNS = new String[] {
			HISTORY_COLUMN_DATE, HISTORY_COLUMN_VALUE };

	public static final String HISTORY_TABLE_NAME = "history";
	public static final String HISTORY_TABLE_CREATE = "CREATE TABLE "
			+ HISTORY_TABLE_NAME + " (" + HISTORY_COLUMN_DATE + " INTEGER, "
			+ HISTORY_COLUMN_VALUE + " TEXT);";

	public static final String STAT_TABLE_NAME = "stats";

	public static final String STAT_COLUMN_PERCENT = "percent";
	public static final String STAT_COLUMN_SAMPLE_COUNT = "sampleCount";
	public static final String STAT_COLUMN_AVERAGE = "average";

	public static final String STAT_TABLE_CREATE = "CREATE TABLE "
			+ STAT_TABLE_NAME + " (" + STAT_COLUMN_PERCENT + " INTEGER, "
			+ STAT_COLUMN_SAMPLE_COUNT + " INTEGER, " + STAT_COLUMN_AVERAGE
			+ " INTEGER );";

	public HistoryDBOpenerHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(HISTORY_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		try {
			if (oldVersion < 2) {
				db.execSQL(HISTORY_TABLE_CREATE);
			}
		} catch (SQLException sqle) {
			// table already exists, this is OK
		}
		try {
			if (oldVersion < 3) {
				db.execSQL(STAT_TABLE_CREATE);
			}
		} catch (SQLException sqle) {
			// table already exists, this is OK
		}
	}
}