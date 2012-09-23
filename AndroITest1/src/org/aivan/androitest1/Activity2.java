package org.aivan.androitest1;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
  public void exitApp(View view) {
    // Do something in response to button

    // finish();

    // Uri number = Uri.parse("tel:5551234");
    // Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
    // startActivity(callIntent);

    // Map point based on address
    // Uri location =
    // Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
    // Or map point based on latitude/longitude
    // Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is
    // zoom level
    // Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
    // PackageManager packageManager = getPackageManager();
    // List<ResolveInfo> activities =
    // packageManager.queryIntentActivities(mapIntent, 0);
    // boolean isIntentSafe = activities.size() > 0;
    // if (isIntentSafe) {
    // startActivity(mapIntent);
    // } else {
    // Log.d("exitApp", "There is not map handler!");
    // }

    // Uri webpage = Uri.parse("http://www.android.com");
    // Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
    //
    // // Always use string resources for UI text. This says something like
    // // "Share this photo with"
    // String title = "Choose how to open URL";
    // // Create and start the chooser
    // Intent chooser = Intent.createChooser(webIntent, title);
    // startActivity(chooser);

    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
    pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/
                                                   // phone numbers
    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
  }

  static final int PICK_CONTACT_REQUEST = 1; // The request code

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Check which request we're responding to
    if (requestCode == PICK_CONTACT_REQUEST) {
      // Make sure the request was successful
      if (resultCode == RESULT_OK) {
        // The user picked a contact.
        // The Intent's data Uri identifies which contact was selected.

        // Do something with the contact here (bigger example below)
        // Get the URI that points to the selected contact
        Uri contactUri = data.getData();
        // We only need the NUMBER column, because there will be only one row in
        // the result
        String[] projection = { Phone.NUMBER };

        // Perform the query on the contact to get the NUMBER column
        // We don't need a selection or sort order (there's only one result for
        // the given URI)
        // CAUTION: The query() method should be called from a separate thread
        // to avoid blocking
        // your app's UI thread. (For simplicity of the sample, this code
        // doesn't do that.)
        // Consider using CursorLoader to perform the query.
        Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
        cursor.moveToFirst();

        // Retrieve the phone number from the NUMBER column
        int column = cursor.getColumnIndex(Phone.NUMBER);
        String number = cursor.getString(column);
        Log.d("activity2", "Number is: " + number);
        // Do something with the phone number...
      }
    }
  }

  /* (non-Javadoc)
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();

    TextView view = (TextView) findViewById(R.id.textView2);
    view.setMovementMethod(new ScrollingMovementMethod());
    view.setText(new HistoryDAO(this).getLastRecords(500));

  }
  
  
}
