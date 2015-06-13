package com.atooma.plugin.macaccelerometer.triggers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.atooma.plugin.AlarmBasedTrigger;
import com.atooma.plugin.IntentBasedTrigger;
import com.atooma.plugin.ParameterBundle;
import com.atooma.plugin.Schedule;
import com.atooma.plugin.macaccelerometer.Constants;
import com.atooma.plugin.macaccelerometer.R;

/**
 * Created by Gabriele on 13/06/15.
 */
public class TR_Tilted extends IntentBasedTrigger {

    @Override
    public void onRevoke(String s) {

    }

    private static final String TAG = "TR_Tilted";

    public TR_Tilted(Context context, String id, int version) {
        super(context, id, version);
    }

    @Override
    public void defineUI() {
        setIcon(R.drawable.plugin_icon_normal);
        setTitle(R.string.tilted);
    }

    @Override
    public void declareParameters() {
        addParameter(R.string.change_from_normal_to_tilted,
                R.string.change_from_normal_to_tilted_null, "NORMAL_TILTED", "BOOLEAN", false, null);
    }

   @Override
    public void declareVariables() {
        //addVariable(R.string.current_status, "STATUS", "STRING");
    }

    @Override
    public void onReceive(String ruleId, ParameterBundle parameters, Bundle bundle) {
        Log.i(TAG, "Received broadcast");
        //int status = bundle.getInt("status");

//        ParameterBundle variables = new ParameterBundle();
//
//        if (status == Constants.NORMAL_TILTED
//                && (boolean)parameters.get("NORMAL_TILTED")) {
//
//            //variables.put("status", "tilted");
//            trigger(ruleId, variables);
//
//        } else if (status == Constants.TILTED_NORMAL
//                       && (boolean)parameters.get("TILTED_NORMAL")) {
//
//            //variables.put("status", "normal");
//            trigger(ruleId, variables);
//        }

        trigger(ruleId, new ParameterBundle());
    }

    @Override
    public String getIntentFilter() throws RemoteException {
        return Constants.CHANGE_INTENT;
    }
}
