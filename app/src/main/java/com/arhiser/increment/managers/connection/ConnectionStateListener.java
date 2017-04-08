package com.arhiser.increment.managers.connection;

/**
 * Created by arhis on 14.10.2016.
 */
public interface ConnectionStateListener {
    void onConnectionStateChanged(ConnectionManager.ConnectionState connectionState);
}
