package org.aivan.androitest1;

import org.aivan.androitest1.db.HistoryDAO;
import org.aivan.androitest1.stats.StatisticsPercentageBasic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	String className = MainActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(className, "onCreate called");
		setContentView(R.layout.activity_main);
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
	}

	private void refreshServiceRunningStatus() {
		boolean serviceRunning = ServiceTools.isServiceRunning(this,
				"org.aivan.androitest1.IvanService");

		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setText("Service is " + (serviceRunning ? "" : "not ")
				+ " running");

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

		Intent intent = new Intent(this, IvanService.class);
		startService(intent);

		refreshServiceRunningStatus();
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

		Toast.makeText(this, "Data cleanup complete!", Toast.LENGTH_LONG)
				.show();
	}

	static StatisticsPercentageBasic stats = new StatisticsPercentageBasic();

	public void recalculateStats(View view) {
		Log.d(className, "recalculateStats");

		stats = new StatisticsPercentageBasic();

		HistoryDAO historyDao = new HistoryDAO(this);
		
		historyDao.iterateRecords(stats);
		stats.fillTheblanks();

		Toast.makeText(this, "Statistics recalculated!", Toast.LENGTH_LONG)
				.show();

		stats.store(historyDao);
		
		Toast.makeText(this, "Statistics stored!", Toast.LENGTH_LONG)
		.show();

		Log.d(className, "Statistics dump:\n" + stats.dump());
	}

	public void calculatePrediction(View view) {
		Log.d(className, "calculatePrediction");
	}

}
