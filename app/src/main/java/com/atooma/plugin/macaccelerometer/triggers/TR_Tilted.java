package com.atooma.plugin.macaccelerometer.triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;

import com.atooma.plugin.AlarmBasedTrigger;
import com.atooma.plugin.ParameterBundle;
import com.atooma.plugin.Schedule;
import com.atooma.plugin.macaccelerometer.ATOOMAACCELEROMETERMODULE;
import com.atooma.plugin.macaccelerometer.Constants;
import com.atooma.plugin.macaccelerometer.R;
import com.atooma.sdk.IAtoomaService;

/**
 * Created by Gabriele on 13/06/15.
 */
public class TR_Tilted extends AlarmBasedTrigger {

    private static final String TAG = "TR_Tilted";

    private BroadcastReceiver receiver;
    private IAtoomaService service;

    private String ruleId;
    private ParameterBundle params;
    private String moduleId;


    public TR_Tilted(Context context, final String id, int version) {
        super(context, id, version);

        moduleId = ATOOMAACCELEROMETERMODULE.MODULE_ID;

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.CHANGE_INTENT);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Received broadcast");

                if (service != null) {
                    int status = intent.getIntExtra("status", -1);
                    if (!(boolean)params.get("TILTED") &&
                            status > -1) {

                        try {
                            service.trigger(moduleId, id, ruleId, params);
                        } catch (RemoteException var4) {
                            var4.printStackTrace();
                        }

                    }
                }
            }
        };

        context.registerReceiver(receiver, filter);
    }

    @Override
    public void defineUI() {
        setIcon(R.drawable.plugin_icon_normal);
        setTitle(R.string.tilted);
    }

    @Override
    public void declareParameters() {
        addParameter(R.string.authentication,
                R.string.authentication, "ACCESS", "PLUGIN", true,
                "com.atooma.plugin.macaccelerometer.AccessServer");

        addParameter(R.string.change_from_normal_to_tilted,
                R.string.change_from_normal_to_tilted_null, "TILTED", "BOOLEAN", false, null);
    }

   @Override
    public void declareVariables() {
        //addVariable(R.string.current_status, "STATUS", "STRING");
    }

    @Override
    public void timeout(IAtoomaService atoomaService,
                        String rId, ParameterBundle parameters) throws RemoteException {
        service = atoomaService;
        ruleId  = rId;
        params  = parameters;

        onTimeout(ruleId, parameters);
    }

    @Override
    public void onTimeout(String ruleId, ParameterBundle parameters) {
        Log.i(TAG, "Ruleid: " + ruleId);

    }

    @Override
    public void onRevoke(String s) {

    }

    @Override
    public Schedule getScheduleInfo() throws RemoteException {
        return new Schedule.Builder().exact(false).triggerAtTime(System.currentTimeMillis()).build();
    }
}
