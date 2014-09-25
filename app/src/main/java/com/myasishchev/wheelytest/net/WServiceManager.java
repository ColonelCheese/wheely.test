package com.myasishchev.wheelytest.net;

import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Messenger;

/**
 * Created by MyasishchevA on 25.09.2014.
 */
public class WServiceManager {

    public static void stop(Application application, Class<? extends WService> clazz) {
        Intent intent = new Intent(application, clazz);
        application.stopService(intent);
    }

    public static void start(Application application, Intent intent) {
        application.startService(intent);
    }

    public static Intent intent(Application application, String wsurl, Class<? extends WService> clazz) {
        Intent intent = new Intent(application, clazz);
        intent.setAction(WService.ACTION_CONNECT);
        intent.putExtra(WService.EXTRAS_KEY_URL, wsurl);
        return intent;
    }

    public static Intent intent(Application application, Location location, Class<? extends WService> clazz) {
        Intent intent = new Intent(application, clazz);
        intent.setAction(WService.ACTION_LOCATION);
        intent.putExtra(WService.EXTRAS_KEY_LOCATION, location);
        return intent;
    }
}
