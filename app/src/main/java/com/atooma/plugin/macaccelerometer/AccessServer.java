package com.atooma.plugin.macaccelerometer;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;

import com.atooma.plugin.AtoomaParams;
import com.atooma.plugin.macaccelerometer.services.RegistrationIntentService;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

import javax.xml.transform.Result;

/**
 * Created by Gabriele on 13/06/15.
 */
public class AccessServer extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "AccessServer" ;

    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private boolean mIntentInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkIfAuthed()) {
            SharedPreferences sp = getSharedPreferences("Prefs", Context.MODE_MULTI_PROCESS);
            String email = sp.getString("email", "");

            Intent result = new Intent();
            result.putExtra(AtoomaParams.ACTIVITY_RESULT_KEY, email);
            sp.edit().putString("AutenticatedText", email).apply();
            setResult(RESULT_OK, result);
            finish();
        }

        setContentView(R.layout.main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope("profile"))
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    private boolean checkIfAuthed() {
        SharedPreferences sp = getSharedPreferences("Prefs", Context.MODE_MULTI_PROCESS);
        return sp.getBoolean("authed", false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;

        final String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        final SharedPreferences sp = getSharedPreferences("Prefs", Context.MODE_MULTI_PROCESS);
        sp.edit().putString("email", email).apply();

        new GetIdTokenTask(new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Constants.REGISTERED) {
                    Intent result = new Intent();
                    result.putExtra(AtoomaParams.ACTIVITY_RESULT_KEY, email);
                    sp.edit().putString("AutenticatedText", email).apply();
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        }).execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private class GetIdTokenTask extends AsyncTask<String, Void, String> {
        ResultReceiver receiver;

        public GetIdTokenTask(ResultReceiver receiver) {
            super();

            this.receiver = receiver;
        }

        @Override
        protected String doInBackground(String... params) {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String scopes = "audience:server:client_id:" + Constants.SERVER_CLIENT_ID; // Not the app's client ID.
            String idToken = "";
            try {
                idToken = GoogleAuthUtil.getToken(getApplicationContext(), account, scopes);
            } catch (GoogleAuthException e) {
                Log.e(TAG, "Error retrieving ID token.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving ID token.", e);
            }
            return idToken;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "ID token: " + result);

            Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
            intent.putExtra("idToken", result);
            intent.putExtra("receiver", receiver);
            startService(intent);
        }

    }

}
