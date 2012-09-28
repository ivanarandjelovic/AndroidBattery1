package org.aivan.androitest1;

import org.aivan.androitest1.db.HistoryDAO;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class IvanService extends Service {

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

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.RTC, AndroBatConfiguration.ESTIMATE_UPDATE_INTERVAL_IN_MINUTES
        * AndroBatConfiguration.MS_PER_MINUTE, AndroBatConfiguration.ESTIMATE_UPDATE_INTERVAL_IN_MINUTES
        * AndroBatConfiguration.MS_PER_MINUTE,
        PendingIntent.getService(this, 0, new Intent(this, IvanService.class), PendingIntent.FLAG_UPDATE_CURRENT));

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
    String prediction = MainActivity.getPrediction(context);
    CharSequence contentText = "Battery: " + lastLevel + " %" + "\nRemaining: " + (prediction == null ? "N/A" : prediction);

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

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(PendingIntent.getService(this, 0, new Intent(this, IvanService.class), PendingIntent.FLAG_UPDATE_CURRENT));

    cancelNotificationIcon();

    unregisterReceiver(this.mBatInfoReceiver);
  }

  static long lastLevel = Long.MIN_VALUE;

  private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d("mBatInfoReceiver", "onReceive");
      int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
      int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
      Log.d("mBatInfoReceiver", "level is " + level);
      Log.d("mBatInfoReceiver", "scale is " + scale);
      long time = System.currentTimeMillis();

      int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
      boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

      int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
      boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
      boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
      Log.d("mBatInfoReceiver", "isCharging = " + isCharging);
      Log.d("mBatInfoReceiver", "usbCharge = " + usbCharge);
      Log.d("mBatInfoReceiver", "acCharge = " + acCharge);

      HistoryDAO historyDAO = new HistoryDAO(context);

      // Write new battery level only if it has changed since last one
      // written to the database
      if (level >= AndroBatConfiguration.MIN_BATTERY_LEVEL && level <= AndroBatConfiguration.MAX_BATERY_LEVEL) {
        // Ok, valid level values, now check if we have lastLevel stored
        // in
        // static variable
        if ((lastLevel == Long.MIN_VALUE && levelNotSameAsInPreferences(context, time, level))
            || (lastLevel != Long.MIN_VALUE && lastLevel != level)) {
          historyDAO.addHistoryRecord(time, level);

          // Record level in preferences, in case our class is
          // unloaded
          saveLevelToPreferences(context, level, time);

          updateNotificationIcon();

        }

        // Record last level in a static field (to save lookup to
        // preferences, whenever possible (whenever the class
        // is still in memory)
        lastLevel = level;

        if (getOldIsCharningPreferences(context) == true && !isCharging && !levelNotSameAsInPreferences(context, time, level)) {
          // We stopped charging, update last DB history record to
          // current time since this is the moment when we start
          // discharing the battery
          historyDAO.updateLastHistoryRecordTime(time);
        }
        if (getOldIsCharningPreferences(context) == false && isCharging && IsItTimeToRecalculateStats(context)) {
          // We started charning, now is the time to recalculate
          // statistics!!!
          MainActivity.recalculateStatistics(context);
          saveStatsTimeToPreferences(context, time);
        }

      }
      saveIsChargingToPreferences(context, isCharging);

      updateNotificationIcon();
    }

    private boolean IsItTimeToRecalculateStats(Context context) {
      SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
      return (System.currentTimeMillis() - prefs.getLong(AndroBatConfiguration.PREFERENCES_STATS_TIMESTAMP, 0)) > AndroBatConfiguration.RECALC_STATS_PERIOD;
    }
    
    private void saveStatsTimeToPreferences(Context context, long time) {
      SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = prefs.edit();
      editor.putLong(AndroBatConfiguration.PREFERENCES_STATS_TIMESTAMP, time);
      editor.commit();
    }

    private void saveIsChargingToPreferences(Context context, boolean isCharging) {
      SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = prefs.edit();
      editor.putBoolean(AndroBatConfiguration.PREFERENCES_IS_CHARING, isCharging);
      editor.commit();
    }

    private boolean getOldIsCharningPreferences(Context context) {
      SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
      return prefs.getBoolean(AndroBatConfiguration.PREFERENCES_IS_CHARING, false);
    }

    private void saveLevelToPreferences(Context context, int level, long time) {
      SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = prefs.edit();
      editor.putLong(AndroBatConfiguration.PREFERENCES_DATE, time);
      editor.putInt(AndroBatConfiguration.PREFERENCES_LEVEL, level);
      editor.commit();
    }

    private boolean levelNotSameAsInPreferences(Context context, long time, int level) {
      boolean result = false;
      SharedPreferences prefs = context.getSharedPreferences("IvanService", Context.MODE_PRIVATE);
      long oldTime = prefs.getLong(AndroBatConfiguration.PREFERENCES_DATE, 0);
      int oldLevel = prefs.getInt(AndroBatConfiguration.PREFERENCES_LEVEL, -1);

      if (oldTime == 0 || oldLevel < 0 || oldLevel != level || (time - oldTime > (AndroBatConfiguration.MAX_MS_PER_PERCENT))) {
        // Ok, we have some old value but it's not the same as the new
        // one or
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
    Log.d(TAG, "onBind");

    return null;
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

}
