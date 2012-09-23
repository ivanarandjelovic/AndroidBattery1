package org.aivan.androitest1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDBOpenerHelper extends SQLiteOpenHelper {

  private static final String DICTIONARY_TABLE_NAME = "history";
  private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" + "date INTEGER, "
      + "value TEXT);";

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