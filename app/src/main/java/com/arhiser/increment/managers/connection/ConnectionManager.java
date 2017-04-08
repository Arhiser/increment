package com.arhiser.increment.managers.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

/**
 * Created by arhis on 13.03.2016.
 */
public class ConnectionManager {

    private static final String KEY_OFFLINE_MODE_ENABLED = "ru.salon.managers.offline.e" + ".OFFLINE_MODE_ENABLED";

    private static ConnectionManager instance;

    private Context context;
    private ConnectionState state;

    private ArrayList<ConnectionStateListener> listeners;

    public enum ConnectionState {
        ONLINE, OFFLINE, LOST
    }

    public static void init(Context context) {
        instance = new ConnectionManager(context);
    }

    public static ConnectionManager getInstance() {
        return instance;
    }

    private ConnectionManager(Context context) {
        this.context = context;
        this.state = hasActiveConnection() ? ConnectionState.ONLINE : ConnectionState.OFFLINE;
        listeners = new ArrayList<>();
    }

    void onReceivedConnectionChanged() {
        if (state == ConnectionState.ONLINE && !hasActiveConnection()) {
            state = ConnectionState.LOST;
        }
        if (hasActiveConnection()) {
            state = ConnectionState.ONLINE;
        }

        dispatchStateChange();
    }

    void setOfflineState() {
        if (state == ConnectionState.LOST) {
            state = ConnectionState.OFFLINE;
            dispatchStateChange();
        }
    }

    public boolean isOnline() {
        return state == ConnectionState.ONLINE;
    }

    private void dispatchStateChange() {
        for (ConnectionStateListener listener: listeners) {
            listener.onConnectionStateChanged(state);
        }
    }

    private boolean hasActiveConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return activeNetInfo != null;
    }

    public void addConnectionStateListener(ConnectionStateListener listener) {
        listeners.add(listener);
    }

    public void removeConnectionStateListener(ConnectionStateListener listener) {
        listeners.remove(listener);
    }
}
