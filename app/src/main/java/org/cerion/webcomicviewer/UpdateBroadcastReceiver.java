package org.cerion.webcomicviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


//TODO, change to local broadcast
public class UpdateBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = UpdateBroadcastReceiver.class.getSimpleName();
    public static final String UPDATE_ACTION = "updateAction";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive");
    }
}
