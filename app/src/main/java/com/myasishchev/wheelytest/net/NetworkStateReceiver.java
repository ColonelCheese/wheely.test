package com.myasishchev.wheelytest.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.myasishchev.wheelytest.model.WLocationManager;
import com.myasishchev.wheelytest.model.WSocketManager;

public class NetworkStateReceiver extends BroadcastReceiver {

    private WSocketManager socketManager;
    private WLocationManager locationManager;

    private WSocketManager.IConnectionListener callback;

    @Override
    public void onReceive(Context context, Intent intent) {
        socketManager = WSocketManager.get(context);
        locationManager = WLocationManager.get(context);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            callback = new WSocketManager.IConnectionListener() {
                @Override
                public void onConnectionOpen() {
                    socketManager.sendLocation(locationManager.getLocation());
                    socketManager.delConnectionListener(callback);
                }

                @Override
                public void onConnectionClose(int code, Bundle data) {
                    socketManager.delConnectionListener(callback);
                }
            };
            socketManager.addConnectionListener(callback);
            socketManager.reconnect();
        } else {
            socketManager.disconnect();
        }
    }
}
