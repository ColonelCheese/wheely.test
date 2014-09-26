package com.myasishchev.wheelytest.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.myasishchev.wheelytest.model.WLocationManager;
import com.myasishchev.wheelytest.model.WSocketManager;

public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = NetworkStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            WNetworkManager.get(context).notifyNetworkChanged(activeNetInfo != null && activeNetInfo.isConnected());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
