package com.atooma.plugin.macaccelerometer;

import android.content.Context;
import android.content.SharedPreferences;

import com.atooma.plugin.Module;
import com.atooma.plugin.macaccelerometer.triggers.TR_Tilted;

public class ATOOMAACCELEROMETERMODULE extends Module {

    public static final String MODULE_ID = "ATOOMAACCELEROMETERMODULE";
    public static final int MODULE_VERSION = 4;

    public ATOOMAACCELEROMETERMODULE(Context context, String id, int version) {
        super(context, id, version);
    }

    @Override
    public void registerComponents() {
        registerTrigger(new TR_Tilted(getContext(), "Tilted", 1));
    }

    @Override
    public void defineUI() {
        setIcon(R.drawable.plugin_icon_normal);
        setTitle(R.string.module_name);
    }

    @Override
    public void defineAuth() {
        SharedPreferences sp = getContext().getSharedPreferences("Prefs", Context.MODE_MULTI_PROCESS);
        if (sp.getBoolean("authed", false))
            setAuthenticated(true, sp.getString("email", ""));
        else
            setAuthenticated(false, "");
    }

    @Override
    public void clearCredentials() {
        SharedPreferences sp = getContext().getSharedPreferences("Prefs", Context.MODE_MULTI_PROCESS);
        sp.edit().clear().commit();
    }
}
