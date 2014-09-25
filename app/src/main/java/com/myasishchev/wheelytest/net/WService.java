package com.myasishchev.wheelytest.net;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.myasishchev.wheelytest.R;
import com.myasishchev.wheelytest.model.WSocketManager;
import com.myasishchev.wheelytest.ui.MapActivity;

import org.apache.commons.lang3.StringUtils;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * Created by MyasishchevA on 25.09.2014.
 */
public class WService extends Service {

    public static final String ACTION_CONNECT = "com.myasishchev.wheelytest.socket.connect";
    public static final String ACTION_LOCATION = "com.myasishchev.wheelytest.socket.loaction";

    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int RECEIVE_TIMEOUT = 30 * 1000;
    private static final int RECONNECT_INTERVAL = 30 * 1000;

    private static final String LOG_TAG = WService.class.getSimpleName();

    public static final String EXTRAS_KEY_URL = "url";
    public static final String EXTRAS_KEY_LOCATION = "location";

    public static final String EXTRAS_KEY_TEXT_MESSAGE = "text";
    public static final String EXTRAS_KEY_CODE = "code";

    private String wsuri = "";
    private WebSocket webSocket = new WebSocketConnection();
    private WebSocketConnectionHandler connectionHandler = new WebSocketConnectionHandler() {

        @Override
        public void onOpen() {
            Log.i(LOG_TAG, "Status: Connected to " + wsuri);
            sendOnOpenMessage();
        }

        @Override
        public void onTextMessage(String payload) {
            Log.e(LOG_TAG, "Message received:\n" + payload);
            sendOnTextMessage(payload);
        }

        @Override
        public void onClose(int code, Bundle data) {
            Log.i(LOG_TAG, "Connection lost.");
            sendOnCloseMessage(code, data);
        }

    };

    protected void sendOnOpenMessage() {
        Intent intent = new Intent(WSocketManager.ACTION_SOCKET_OPEN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void sendOnCloseMessage(int code, Bundle data) {
        Intent intent = new Intent(WSocketManager.ACTION_SOCKET_CLOSE);
        intent.putExtras(data);
        intent.putExtra(EXTRAS_KEY_CODE, code);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void sendOnTextMessage(String payload) {
        Intent intent = new Intent(WSocketManager.ACTION_SOCKET_MESSAGE);
        intent.putExtra(EXTRAS_KEY_TEXT_MESSAGE, payload);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "WService:onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null && webSocket.isConnected()) webSocket.disconnect();
        Log.i(LOG_TAG, "WService:onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            startForeground(NOTIFICATION_ID, notification(getApplicationContext()));
            Log.i(LOG_TAG, "onStartCommand:flags:" + flags + ":startId:" + startId);
            onHandleIntent(intent);
            /*if (startId > 1) {
                //stopSelf(startId);
                //onStartRejected(intent);
                Log.i(LOG_TAG, "WService:onStartCommand:rejected");
            } else {
                onServiceStart(intent);
                Log.i(LOG_TAG, "WService:onStartCommand:started");
            }*/
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (StringUtils.isNotEmpty(action)) {
            if (ACTION_CONNECT.equals(action)) {
                if (!isConnected()) onServiceStart(intent);
            } else
            if (ACTION_LOCATION.equals(action)) {
                sendLocation(intent.<Location>getParcelableExtra(EXTRAS_KEY_LOCATION));
            }
        }
    }

    protected void onServiceStart(Intent intent) {
        try {
            this.wsuri = getWSUrl(intent);
            Log.i(LOG_TAG, "Status: Connecting to " + wsuri + "...");
            webSocket.connect(wsuri, connectionHandler, webSocketOptions());
        } catch (WebSocketException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private String getWSUrl(Intent intent) {
        return intent.getExtras().getString(EXTRAS_KEY_URL);
    }

    private static WebSocketOptions webSocketOptions() {
        WebSocketOptions webSocketOptions = new WebSocketOptions();
        webSocketOptions.setSocketConnectTimeout(CONNECTION_TIMEOUT);
        webSocketOptions.setSocketReceiveTimeout(RECEIVE_TIMEOUT);
        //webSocketOptions.setReconnectInterval(RECONNECT_INTERVAL);
        return webSocketOptions;
    }

    private boolean isConnected() {
        return webSocket.isConnected();
    }

    private void sendLocation(Location location) {
        if (isConnected() && location != null) {
            Log.i(LOG_TAG, "sendLocation:lat:" + location.getLatitude() + ":lon:" + location.getLongitude());
            webSocket.sendTextMessage(WSocketManager.locationMessage(location));
        }
    }

    private static Notification notification(Context context) {
        Intent showIntent = new Intent(context, MapActivity.class);
        showIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT works
        int requestId = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestId, showIntent, flags);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("Start foreground service")
                .setSmallIcon(R.drawable.ic_launcher)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        //notification.defaults |= Notification.DEFAULT_SOUND;
        //notification.defaults |= Notification.DEFAULT_VIBRATE;
        //notification.defaults |= Notification.DEFAULT_LIGHTS;

        return notification;
    }
}
