package org.aivan.androitest1;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HistoryDAO {
  
  Context context = null;
  HistoryDBOpenerHelper dbHelper = null;
   
  public HistoryDAO(Context context) {
    super();
    this.context = context;
    dbHelper = new HistoryDBOpenerHelper(context, "historyDB", null, 2);
  }



  public void addHistoryRecord(int level) {
    
    ContentValues values = new ContentValues();
    values.put("date", System.currentTimeMillis());
    values.put("value", level);
    SQLiteDatabase histDB = dbHelper.getWritableDatabase();
    histDB.insert("history",null, values);
    histDB.close();
  }
  
  /**
   * Get Last "count" record from the DB as a simple string, usefull for debugging and
   * monitoring
   * @param recordCount How many last records to return in the result
   * @return String with last "count" records, date and time properly formatted as YYYY-MM-DD HH:MM:SS
   */
  public String getLastRecords(int recordCount) {
    String result = "";
    
    //result = "Test one\ntest 2\n test3";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    SQLiteDatabase histDB = dbHelper.getReadableDatabase();
    Cursor cursor = histDB.query("history", HistoryDBOpenerHelper.columns, "", null, null, null, HistoryDBOpenerHelper.COLUMN_DATE + " desc",""+recordCount); 
    if(cursor != null ) {
      if(cursor.moveToFirst()) {
        do {
          long date = cursor.getLong(cursor.getColumnIndex(HistoryDBOpenerHelper.COLUMN_DATE));
          String value = cursor.getString(cursor.getColumnIndex(HistoryDBOpenerHelper.COLUMN_VALUE));
          
          result += sdf.format(new Date(date))+" : " + value+"\n";
          
         } while(cursor.moveToNext());
      }
      cursor.close();
    }
    histDB.close();
    
    return result;
  }

}
