package com.atooma.plugin.macaccelerometer;

import com.atooma.plugin.Module;
import com.atooma.sdk.AtoomaPluginService;

/**
 * Created by Gabriele on 13/06/15.
 */
public class ATOOMAACCELEROMETERMODULERegister extends AtoomaPluginService {
    @Override
    public Module getModuleInstance() {
        return new ATOOMAACCELEROMETERMODULE(this,
                ATOOMAACCELEROMETERMODULE.MODULE_ID,
                ATOOMAACCELEROMETERMODULE.MODULE_VERSION);
    }
}