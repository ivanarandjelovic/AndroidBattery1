package org.aivan.androitest1;

import org.aivan.androitest1.db.HistoryDAO;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class IvanService extends Service {

  private static final String PREFERENCES_LEVEL = "level";

  private static final String PREFERENCES_DATE = "date";

  static final String TAG = IvanService.class.getName();

  private static final int ANDRO_BAT_ID = 1;

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

    registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    updateNotificationIcon();

  }

  private void updateNotificationIcon() {
    String ns = Context.NOTIFICATION_SERVICE;
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

    int icon = R.drawable.bat_level_list;
    CharSequence tickerText = null;
    long when = System.currentTimeMillis();

    Notification notification = new Notification(icon, tickerText, when);

    Context context = getApplicationContext();
    CharSequence contentTitle = getResources().getText(R.string.app_name);
    CharSequence contentText = "Current battery level: " + lastLevel + " %";
    ;
    Intent notificationIntent = new Intent(this, MainActivity.class);
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    notification.flags |= Notification.FLAG_ONGOING_EVENT;
    // notification.number = (int) lastLevel;
    notification.iconLevel = (int) lastLevel;

    mNotificationManager.notify(ANDRO_BAT_ID, notification);
  }

  private void cancelNotificationIcon() {
    String ns = Context.NOTIFICATION_SERVICE;
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
    mNotificationManager.cancelAll();
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

    cancelNotificationIcon();

    unregisterReceiver(this.mBatInfoReceiver);
  }

  static long lastLevel = Long.MIN_VALUE;

  private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d("mBatInfoReceiver", "onReceive");
      int level = intent.getIntExtra(PREFERENCES_LEVEL, 0);
      Log.d("mBatInfoReceiver", "level is " + level);
      long time = System.currentTimeMillis();

      // Write new battery level only if it has changed since last one
      // written to the database
      if (level >= AndroBatConfiguration.MIN_BATTERY_LEVEL && level <= AndroBatConfiguration.MAX_BATERY_LEVEL) {
        // Ok, valid level values, now check if we have lastLevel stored in
        // static variable
        if ((lastLevel == Long.MIN_VALUE && levelNotSameAsInPreferences(context, time, level))
            || (lastLevel != Long.MIN_VALUE && lastLevel != level)) {
          new HistoryDAO(context).addHistoryRecord(time, level);
          // Record last level in a static field
          lastLevel = level;
          // Also record level in preferences, in case our class is unloaded
          saveLevelToPreferences(context, level, time);

          updateNotificationIcon();

        }
      }

    }

    private void saveLevelToPreferences(Context context, int level, long time) {
      SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = prefs.edit();
      editor.putLong(PREFERENCES_DATE, time);
      editor.putInt(PREFERENCES_LEVEL, level);
      editor.commit();
    }

    private boolean levelNotSameAsInPreferences(Context context, long time, int level) {
      boolean result = false;
      SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
      long oldTime = prefs.getLong(PREFERENCES_DATE, 0);
      int oldLevel = prefs.getInt(PREFERENCES_LEVEL, -1);

      if (oldTime == 0 || oldLevel < 0 || oldLevel != level || (time - oldTime > (AndroBatConfiguration.MAX_MINUTES_PER_PERCENT))) {
        // Ok, we have some old value but it's not the same as the new one or
        // the time range is too long
        // so this is probably some new reading and store it as new:
        result = true;
      }

      return result;
    }
  };

  @Override
  public IBinder onBind(Intent intent) {
    // TODO Auto-generated method stub
    return null;
  }

}
