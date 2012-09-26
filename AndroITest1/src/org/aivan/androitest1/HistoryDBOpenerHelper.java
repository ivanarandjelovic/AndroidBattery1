package org.aivan.androitest1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDBOpenerHelper extends SQLiteOpenHelper {

	public static final String HISTORY_COLUMN_DATE = "date";
	public static final String HISTORY_COLUMN_VALUE = "value";

	public static final String[] HISTORY_COLUMNS = new String[] { HISTORY_COLUMN_DATE,
			HISTORY_COLUMN_VALUE };

	public static final String HISTORY_TABLE_NAME = "history";
	public static final String HISTORY_TABLE_CREATE = "CREATE TABLE "
			+ HISTORY_TABLE_NAME + " (" + HISTORY_COLUMN_DATE + " INTEGER, "
			+ HISTORY_COLUMN_VALUE + " TEXT);";
	static final String DB_NAME = "historyDB";

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

	}
}