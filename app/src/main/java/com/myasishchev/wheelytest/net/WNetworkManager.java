package com.myasishchev.wheelytest.net;

import android.app.Activity;
import android.content.Context;

import com.myasishchev.wheelytest.WApplication;

import java.util.ArrayList;
import java.util.List;

public class WNetworkManager {

    public interface INetworkListener {
        public void onNetworkStateChanged(boolean isConnected);
    }

    public static WNetworkManager get(Activity activity) {
        return WApplication.get(activity).networkManager();
    }

    public static WNetworkManager get(Context context) {
        return WApplication.get(context).networkManager();
    }

    private final List<INetworkListener> listeners = new ArrayList<INetworkListener>();

    public void addNetworkListener(INetworkListener listener) {
        if (listener != null) listeners.add(listener);
    }

    public void delNetworkListener(INetworkListener listener) {
        if (listener != null) listeners.remove(listener);
    }

    protected void notifyNetworkChanged(boolean isConnected) {
        for (INetworkListener listener: listeners) {
            listener.onNetworkStateChanged(isConnected);
        }
    }
}
