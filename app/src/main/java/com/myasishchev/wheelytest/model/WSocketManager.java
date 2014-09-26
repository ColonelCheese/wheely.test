package com.myasishchev.wheelytest.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.myasishchev.wheelytest.WApplication;
import com.myasishchev.wheelytest.service.WService;
import com.myasishchev.wheelytest.service.WServiceManager;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import de.tavendo.autobahn.WebSocketConnection;

public class WSocketManager {

    private static final String HOST = "mini-mdt.wheely.com";

    private static final String LOG_TAG = WSocketManager.class.getSimpleName();

    public static final String ACTION_SOCKET_OPEN = "com.myasishchev.wheelytest.socket.open";
    public static final String ACTION_SOCKET_CLOSE = "com.myasishchev.wheelytest.socket.close";
    public static final String ACTION_SOCKET_MESSAGE = "com.myasishchev.wheelytest.socket.message";

    public static class IConnectionListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SOCKET_OPEN.equals(intent.getAction())) {
                onConnectionOpen();
            } else
            if (ACTION_SOCKET_CLOSE.equals(intent.getAction())) {
                onConnectionClose(intent.getIntExtra(WService.EXTRAS_KEY_CODE, 0), intent.getExtras());
            }
        }

        public void onConnectionOpen() {}
        public void onConnectionClose(int code, Bundle data) {}
    }

    public static class IMessagesListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_SOCKET_MESSAGE.equals(intent.getAction())) {
                onTextMessage(intent.getStringExtra(WService.EXTRAS_KEY_TEXT_MESSAGE));
            }
        }

        public void onTextMessage(String payload) {}
    }

    private String wsuri = "";
    private final Application application;

    public WSocketManager(Application application) {
        this.application = application;
    }

    public static WSocketManager get(Context context) {
        return WApplication.get(context).requestManager();
    }

    public static WSocketManager get(Activity activity) {
        return WApplication.get(activity).requestManager();
    }

    public void connect(String username, String password) {
        wsuri = "ws://" + HOST + File.separator + String.format("?username=%s&password=%s", username, password);
        WServiceManager.start(application, WServiceManager.intent(application, wsuri, WService.class));
    }

    /*public void reconnect() {
        WServiceManager.start(application, WServiceManager.intent(application, wsuri, WService.class));
    }*/

    public void disconnect() {
       WServiceManager.stop(application, WService.class);
    }

    /*public void sendLocation(Location location) {
        WServiceManager.start(application, WServiceManager.intent(application, location, WService.class));
    }*/

    public static String locationMessage(Location location) {
        return String.format("{\"lat\": %s, \"lon\":%s}", location.getLatitude(), location.getLongitude());
    }

    public void addMessagesListener(IMessagesListener callback) {
        IntentFilter iff = new IntentFilter();
        iff.addAction(ACTION_SOCKET_MESSAGE);
        LocalBroadcastManager.getInstance(application).registerReceiver(callback, iff);
    }

    public void delMessagesListener(IMessagesListener listener) {
        LocalBroadcastManager.getInstance(application).unregisterReceiver(listener);
    }

    public void addConnectionListener(IConnectionListener callback) {
        IntentFilter iff = new IntentFilter();
        iff.addAction(ACTION_SOCKET_CLOSE);
        iff.addAction(ACTION_SOCKET_OPEN);
        LocalBroadcastManager.getInstance(application).registerReceiver(callback, iff);
    }

    public void delConnectionListener(IConnectionListener callback) {
        LocalBroadcastManager.getInstance(application).unregisterReceiver(callback);
    }

    private AlertDialog alertDialog;

    public void handleConnectionClose(int code, Bundle data, Activity activity) {
        if (data != null && activity != null) {

            //int statusCode = data.getInt(WebSocketConnection.EXTRA_STATUS_CODE);

            String statusMessage = data.getString(WebSocketConnection.EXTRA_STATUS_MESSAGE);
            if (StringUtils.isEmpty(statusMessage)) {
                statusMessage = data.getString(WebSocketConnection.EXTRA_REASON);
            }

            if (alertDialog != null) alertDialog.dismiss();
            alertDialog = new AlertDialog.Builder(activity)
                    .setMessage(statusMessage /*String.format("Status code:%s \nStatus message:%s", statusCode, statusMessage)*/)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    }).create();
            alertDialog.show();
        }
    }
}
