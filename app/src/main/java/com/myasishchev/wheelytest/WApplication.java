package com.myasishchev.wheelytest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.myasishchev.wheelytest.model.WLocationManager;

public class WApplication extends Application {

    private WLocationManager locationManager;

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
}
