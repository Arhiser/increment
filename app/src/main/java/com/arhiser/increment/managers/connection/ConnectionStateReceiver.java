package com.arhiser.increment.managers.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectionStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        if (connectionManager != null) {
            connectionManager.onReceivedConnectionChanged();
        }
    }
}