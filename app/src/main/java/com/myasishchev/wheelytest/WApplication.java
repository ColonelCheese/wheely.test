package com.myasishchev.wheelytest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.myasishchev.wheelytest.model.WLocationManager;
import com.myasishchev.wheelytest.net.WNetworkManager;
import com.myasishchev.wheelytest.model.WSocketManager;

public class WApplication extends Application {

    private WLocationManager locationManager;
    private WSocketManager requestManager;
    private WNetworkManager networkManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static WApplication get(Context context) {
        if (context instanceof WApplication) {
            return (WApplication) context;
        } else {
            return (WApplication) context.getApplicationContext();
        }
    }

    public static WApplication get(Activity activity) {
        return WApplication.get(activity.getApplicationContext());
    }

    public WLocationManager locationManager() {
        if (locationManager == null)
            locationManager = new WLocationManager(this);
        return locationManager;
    }

    public WSocketManager requestManager() {
        if (requestManager == null)
            requestManager = new WSocketManager(this);
        return requestManager;
    }

    public WNetworkManager networkManager() {
        if (networkManager == null) {
            networkManager = new WNetworkManager();
        }
        return networkManager;
    }
}
