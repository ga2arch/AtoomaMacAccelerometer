package com.atooma.plugin.macaccelerometer.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.atooma.plugin.macaccelerometer.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Gabriele on 13/06/15.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String gcmToken = instanceID.getToken(Constants.SERVER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + gcmToken);

                ResultReceiver receiver = intent.getParcelableExtra("receiver");
                String idToken = intent.getStringExtra("idToken");
                sendTokens(idToken, gcmToken, receiver);

          }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
         }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
    }

    void sendTokens(String idToken, String gcmToken, ResultReceiver receiver) {
        String data = "id_token="+idToken+"&gcm_token="+gcmToken;

        try {
            int code = doPost(Constants.SERVER_URL, data);

            if (code == 200) {
                SharedPreferences sp = getSharedPreferences("Prefs", Context.MODE_MULTI_PROCESS);
                SharedPreferences.Editor spEditor = sp.edit();

                spEditor.putString("idToken", idToken);
                spEditor.putString("gcmToken", gcmToken);
                spEditor.putBoolean("authed", true);

                spEditor.apply();

                receiver.send(Constants.REGISTERED, new Bundle());
            } else {
                receiver.send(Constants.REGISTER_ERROR, new Bundle());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error");
        }
    }

    public int doPost(String url, String requestBody) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setFixedLengthStreamingMode(requestBody.getBytes().length);
        conn.setRequestMethod("POST");

        OutputStream out = null;
        try {
            out = conn.getOutputStream();
            out.write(requestBody.getBytes());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignore.
                }
            }
        }

        return conn.getResponseCode();
    }
}
