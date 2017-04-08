package com.arhiser.increment.managers.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by arhis on 03.04.2017.
 */

public class GpsStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            //UserLocationManager.instance(context).requestSingleUpdate(context);
        }
    }
}
