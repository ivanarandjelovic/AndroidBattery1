package org.aivan.androitest1;

import java.text.DecimalFormat;

import org.aivan.androitest1.db.HistoryDAO;
import org.aivan.androitest1.stats.StatisticsPercentageBasic;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

  static String className = MainActivity.class.getName();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(className, "onCreate called ");
    setContentView(R.layout.activity_main);

    // This is the good place to start our service, in case it's not started
    // yet:
    startIvanService();

    // AlarmManager alarmManager = (AlarmManager)
    // getSystemService(Context.ALARM_SERVICE);
    // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
    // AndroBatConfiguration.ESTIMATE_UPDATE_INTERVAL_IN_MINUTES
    // * AndroBatConfiguration.MS_PER_SECOND,
    // AndroBatConfiguration.ESTIMATE_UPDATE_INTERVAL_IN_MINUTES
    // * AndroBatConfiguration.MS_PER_SECOND,
    // PendingIntent.getBroadcast(this, 0, new Intent(this,
    // AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(className, "onCreateOptionsMenu called");
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
   */
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onRestoreInstanceState(savedInstanceState);
    Log.d(className, "onRestoreInstanceState called");
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    // TODO Auto-generated method stub
    super.onSaveInstanceState(outState);
    Log.d(className, "onSaveInstanceState called");
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy() {
    // AlarmManager alarmManager = (AlarmManager)
    // getSystemService(Context.ALARM_SERVICE);
    // alarmManager.cancel(PendingIntent.getActivity(this, 0, new Intent(this,
    // AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
    //
    // TODO Auto-generated method stub
    super.onDestroy();
    Log.d(className, "onDestroy called");
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    Log.d(className, "onPause called");
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onRestart()
   */
  @Override
  protected void onRestart() {
    // TODO Auto-generated method stub
    super.onRestart();
    Log.d(className, "onRestart called");

  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    Log.d(className, "onResume called");

    refreshServiceRunningStatus();
    calculatePrediction();
  }

  private void refreshServiceRunningStatus() {
    boolean serviceRunning = ServiceTools.isServiceRunning(this, "org.aivan.androitest1.IvanService");

    TextView textView = (TextView) findViewById(R.id.textView1);
    textView.setText("Service is " + (serviceRunning ? "" : "not ") + " running");

    Button startServiceButton = (Button) findViewById(R.id.startService);
    Button stopServiceButton = (Button) findViewById(R.id.stopService);

    startServiceButton.setEnabled((serviceRunning ? false : true));
    stopServiceButton.setEnabled(!startServiceButton.isEnabled());
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onStart()
   */
  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    Log.d(className, "onStart called");
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onStop()
   */
  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    Log.d(className, "onStop called");
  }

  /** Called when the user clicks the Send button */
  public void sendMessage(View view) {
    // Do something in response to button
    Intent intent = new Intent(this, Activity2.class);
    startActivity(intent);
  }

  /** Called when the user clicks the Send button */
  public void startService(View view) {
    // Do something in response to button
    Log.d(className, "startService");

    startIvanService();

    refreshServiceRunningStatus();
  }

  private void startIvanService() {
    Intent intent = new Intent(this, IvanService.class);
    startService(intent);
  }

  public void stopService(View view) {
    // Do something in response to button
    Log.d(className, "stopService");

    Intent intent = new Intent(this, IvanService.class);
    stopService(intent);

    refreshServiceRunningStatus();
  }

  public void dataCleanup(View view) {
    Log.d(className, "dataCleanup");

    new HistoryDAO(this).performDataCleanup();

    Toast.makeText(this, "Data cleanup complete!", Toast.LENGTH_LONG).show();
  }

  static StatisticsPercentageBasic stats = new StatisticsPercentageBasic();

  public void recalculateStats(View view) {
    Log.d(className, "recalculateStats");

    new HistoryDAO(this).performDataCleanup();

    recalculateStatistics(this);

    Toast.makeText(this, "Statistics recalculated and stored!", Toast.LENGTH_SHORT).show();

    Log.d(className, "Statistics dump:\n" + stats.dump(this));
  }

  static protected void recalculateStatistics(Context context) {
    Log.d(className, "recalculateStatistics called");

    stats = new StatisticsPercentageBasic();

    HistoryDAO historyDao = new HistoryDAO(context);

    historyDao.iterateRecords(stats);
    stats.fillTheblanks();

    stats.store(historyDao);
  }

  public void calculatePrediction(View view) {
    calculatePrediction();

  }

  private void calculatePrediction() {
    Log.d(className, "calculatePrediction");

    String remainingTime = getPrediction(this);

    TextView textView = (TextView) findViewById(R.id.textView2);
    if (remainingTime != null) {

      textView.setText(remainingTime);
    } else {
      textView.setText("Estimate is: N/A");

    }
  }

  static protected String getPrediction(Context context) {
    stats = new StatisticsPercentageBasic();

    HistoryDAO historyDao = new HistoryDAO(context);

    stats.load(historyDao);

    int lastLevel = historyDao.getLastBatteryLevel();
    long lastTime = historyDao.getLastBatteryDate();

    long remainingTime = stats.estimateForLevel(lastLevel);

    if (remainingTime != Long.MIN_VALUE) {
      // Reduce for time passed since last level change
      remainingTime -= (System.currentTimeMillis() - lastTime);

      if (remainingTime < 0) {
        remainingTime = 0;
      }

      long days = remainingTime / AndroBatConfiguration.MS_PER_DAY;
      remainingTime = remainingTime % AndroBatConfiguration.MS_PER_DAY;

      long hours = remainingTime / AndroBatConfiguration.MS_PER_HOUR;
      remainingTime = remainingTime % AndroBatConfiguration.MS_PER_HOUR;

      long minutes = remainingTime / AndroBatConfiguration.MS_PER_MINUTE;
      remainingTime = remainingTime % AndroBatConfiguration.MS_PER_MINUTE;

      if (remainingTime > 30 * AndroBatConfiguration.MS_PER_SECOND) {
        minutes++;
      }

      DecimalFormat df = new DecimalFormat("00");
      return ((days > 0 ? days + " days " : "") + df.format(hours) + ":" + df.format(minutes));
    } else {
      return null;
    }
  }

}
