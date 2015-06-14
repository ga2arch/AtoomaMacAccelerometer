package com.atooma.plugin.macaccelerometer.services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.atooma.plugin.macaccelerometer.Constants;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Gabriele on 13/06/15.
 */
public class GCMListenerService extends GcmListenerService {

    private static final String TAG = "GcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String status = data.getString("status");
        Log.i(TAG, "Received status: " + status);

        Intent intent = new Intent();
        intent.setAction(Constants.CHANGE_INTENT);
        intent.putExtra("status", Integer.parseInt(status));

        sendBroadcast(intent);
    }
}
