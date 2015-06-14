package com.atooma.plugin.macaccelerometer.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.atooma.plugin.macaccelerometer.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

/**
 * Created by Gabriele on 13/06/15.
 */

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

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
        String data = "idToken="+idToken+"&gcmToken="+gcmToken;

        try {
            doPost(Constants.SERVER_URL, data);

            SharedPreferences sp = getSharedPreferences("Prefs", Context.MODE_MULTI_PROCESS);
            sp.edit().putString("idToken", idToken).commit();
            sp.edit().putString("gcmToken", gcmToken).commit();
            sp.edit().putBoolean("authed", true).commit();

            receiver.send(1, new Bundle());

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

        int responseCode = conn.getResponseCode();

        return responseCode;
    }
}
