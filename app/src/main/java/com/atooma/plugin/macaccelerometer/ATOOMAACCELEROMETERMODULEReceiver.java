package com.atooma.plugin.macaccelerometer;

import com.atooma.sdk.AtoomaRegistrationReceiver;

/**
 * Created by Gabriele on 13/06/15.
 */
public class ATOOMAACCELEROMETERMODULEReceiver extends AtoomaRegistrationReceiver {

    @Override
    public Class getRegisterServiceClass() {
        return ATOOMAACCELEROMETERMODULERegister.class;
    }

}