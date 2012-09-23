package org.aivan.androitest1;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class IvanService extends Service {

  static final String TAG = IvanService.class.getName();

  @Override
  public IBinder onBind(Intent intent) {
    // TODO Auto-generated method stub
    Log.d(TAG, "onBind");
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onCreate()
   */
  @Override
  public void onCreate() {
    // TODO Auto-generated method stub
    super.onCreate();
    Log.d(TAG, "onCreate");

    // AlarmManager alarmManager = (AlarmManager)
    // getSystemService(Context.ALARM_SERVICE);
    // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, 10000,
    // PendingIntent.getService(this, 0 , new Intent(this, IvanService.class),
    // PendingIntent.FLAG_UPDATE_CURRENT ));

    registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onStart(android.content.Intent, int)
   */
  @Override
  public void onStart(Intent intent, int startId) {
    // TODO Auto-generated method stub
    super.onStart(intent, startId);

    Log.d(TAG, "onStart");

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * android.app.Service#onConfigurationChanged(android.content.res.Configuration
   * )
   */
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    // TODO Auto-generated method stub
    super.onConfigurationChanged(newConfig);
    Log.d(TAG, "onConfigurationChanged");
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onDestroy()
   */
  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
    Log.d(TAG, "onDestroy");

    // AlarmManager alarmManager = (AlarmManager)
    // getSystemService(Context.ALARM_SERVICE);
    // alarmManager.cancel( PendingIntent.getService(this, 0 , new Intent(this,
    // IvanService.class), PendingIntent.FLAG_UPDATE_CURRENT ));

    unregisterReceiver(this.mBatInfoReceiver);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onLowMemory()
   */
  @Override
  public void onLowMemory() {
    // TODO Auto-generated method stub
    super.onLowMemory();
    Log.d(TAG, "onLowMemory");
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onRebind(android.content.Intent)
   */
  @Override
  public void onRebind(Intent intent) {
    // TODO Auto-generated method stub
    super.onRebind(intent);
    Log.d(TAG, "onRebind");
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // TODO Auto-generated method stub
    Log.d(TAG, "onStartCommand");
    return super.onStartCommand(intent, flags, startId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onUnbind(android.content.Intent)
   */
  @Override
  public boolean onUnbind(Intent intent) {
    // TODO Auto-generated method stub
    Log.d(TAG, "onUnbind");
    return super.onUnbind(intent);
  }

  private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d("mBatInfoReceiver", "onReceive");
      int level = intent.getIntExtra("level", 0);
      Log.d("mBatInfoReceiver", "level is " + level);

      new HistoryDAO(context).addHistoryRecord(level);
     
      
      // TODO: this is debug stuff
      //Toast.makeText(context, "Added battery level "+level+" into the DB!", Toast.LENGTH_SHORT).show();
      
    }
  };

}
