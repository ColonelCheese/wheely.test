package com.myasishchev.wheelytest.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.myasishchev.wheelytest.WApplication;
import com.myasishchev.wheelytest.ui.LoginActivity;

import java.io.File;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

public class WSocketManager {

    private static final String TAG = WSocketManager.class.getSimpleName();

    private static final String HOST = "mini-mdt.wheely.com";

    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int RECEIVE_TIMEOUT = 30 * 1000;
    private static final int RECONNECT_INTERVAL = 30 * 1000;

    private AlertDialog alertDialog;

    public void handleConnectionClose(int code, Bundle data, Activity activity) {
        if (data != null && activity != null) {
            String statusMessage = data.getString(WebSocketConnection.EXTRA_STATUS_MESSAGE);
            int statusCode = data.getInt(WebSocketConnection.EXTRA_STATUS_CODE);
            if (alertDialog != null) alertDialog.dismiss();
            alertDialog = new AlertDialog.Builder(activity)
                    .setMessage(String.format("Status code:%s \nStatus message:%s", statusCode, statusMessage))
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).create();
            alertDialog.show();
        }
    }

    public interface IConnectionListener {
        public void onConnectionOpen();
        public void onConnectionClose(int code, Bundle data);
    }

    private WebSocket webSocket = new WebSocketConnection();

    public WSocketManager(Application application) {

    }

    public static WSocketManager get(Context context) {
        return WApplication.get(context).requestManager();
    }

    public static WSocketManager get(Activity activity) {
        return WApplication.get(activity).requestManager();
    }

    public void connect(String username, String password, final IConnectionListener callback) {
        final String wsuri = "ws://" + HOST + File.separator + String.format("?username=%s&password=%s", username, password);
        Log.d(TAG, "Status: Connecting to " + wsuri + "...");
        try {
            webSocket.connect(wsuri, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    if (callback != null)
                        callback.onConnectionOpen();
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.e(TAG, "Message received:\n" + payload);
                }

                @Override
                public void onClose(int code, Bundle data) {
                    Log.i(TAG, "Connection lost.");
                    if (callback != null)
                        callback.onConnectionClose(code, data);
                }

            }, webSocketOptions());
        } catch (WebSocketException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public boolean isConnected() {
        return webSocket.isConnected();
    }

    public void disconnect() {
        webSocket.disconnect();
    }

    public void sendLocation(Location location) {
        if (isConnected() && location != null)
            webSocket.sendTextMessage(locationMessage(location));
    }

    private static String locationMessage(Location location) {
        return String.format("{\"lat\":\"%s\", \"lon\":\"%s\"}", location.getLatitude(), location.getLongitude());
    }

    private static WebSocketOptions webSocketOptions() {
        WebSocketOptions webSocketOptions = new WebSocketOptions();
        webSocketOptions.setSocketConnectTimeout(CONNECTION_TIMEOUT);
        webSocketOptions.setSocketReceiveTimeout(RECEIVE_TIMEOUT);
        webSocketOptions.setReconnectInterval(RECONNECT_INTERVAL);
        return webSocketOptions;
    }
}
