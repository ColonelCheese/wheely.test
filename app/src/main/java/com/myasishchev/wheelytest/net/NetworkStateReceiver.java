package com.myasishchev.wheelytest.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.myasishchev.wheelytest.model.WLocationManager;
import com.myasishchev.wheelytest.model.WSocketManager;

/**
 * Created by MyasishchevA on 15.05.2014.
 */
public class NetworkStateReceiver extends BroadcastReceiver implements WSocketManager.IConnectionListener {

    private WSocketManager socketManager;
    private WLocationManager locationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        socketManager = WSocketManager.get(context);
        locationManager = WLocationManager.get(context);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        boolean isNetworkPresent = activeNetInfo != null && activeNetInfo.isConnected();
        if (isNetworkPresent && !socketManager.isConnected()) {
            socketManager.addConnectionListener(this);
            socketManager.reconnect();
        } else if (socketManager.isConnected()) {
            socketManager.disconnect();
        }
    }

    @Override
    public void onConnectionOpen() {
        socketManager.sendLocation(locationManager.getLocation());
        socketManager.delConnectionListener(this);
    }

    @Override
    public void onConnectionClose(int code, Bundle data) {
        socketManager.delConnectionListener(this);
    }
}
