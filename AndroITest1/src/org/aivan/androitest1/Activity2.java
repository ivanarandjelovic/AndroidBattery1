package org.aivan.androitest1;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Activity2 extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_activity2);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_activity2, menu);
    return true;
  }

  /** Called when the user clicks the Exit button */
  public void refreshData(View view) {
    refreshLastData();
  }

  static final int PICK_CONTACT_REQUEST = 1; // The request code


  /* (non-Javadoc)
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();

    refreshLastData();

  }

  private void refreshLastData() {
    TextView view = (TextView) findViewById(R.id.textView2);
    view.setMovementMethod(new ScrollingMovementMethod());
    view.setText(new HistoryDAO(this).getLastRecords(500));
  }
  
  
}
