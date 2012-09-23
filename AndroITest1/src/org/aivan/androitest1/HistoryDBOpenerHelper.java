package org.aivan.androitest1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDBOpenerHelper extends SQLiteOpenHelper {

  public static final String COLUMN_DATE = "date";
  public static final String COLUMN_VALUE = "value";
  
  public static final String[] columns = new String[]{ COLUMN_DATE, COLUMN_VALUE};

  private static final String DICTIONARY_TABLE_NAME = "history";
  private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" + COLUMN_DATE + " INTEGER, "
      + COLUMN_VALUE + " TEXT);";
  
  

  public HistoryDBOpenerHelper(Context context, String name, CursorFactory factory, int version) {
    super(context, name, factory, version);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(DICTIONARY_TABLE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // TODO Auto-generated method stub

  }
}