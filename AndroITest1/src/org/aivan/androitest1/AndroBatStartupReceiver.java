package org.aivan.androitest1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AndroBatStartupReceiver extends BroadcastReceiver {
    public AndroBatStartupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    	// Simply start our data collecting service
        Intent serviceIntent = new Intent(context, IvanService.class);
        context.startService(serviceIntent);
    }
}
