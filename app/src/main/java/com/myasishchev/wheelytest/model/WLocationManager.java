package com.myasishchev.wheelytest.model;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.myasishchev.wheelytest.WApplication;

import java.util.ArrayList;
import java.util.List;

public class WLocationManager implements android.location.LocationListener {

    private static final String LOG_TAG = WLocationManager.class.getSimpleName();

    // Update frequency in minutes
    private static final int     UPDATE_INTERVAL_IN_MINUTES   = 1;
    // Update frequency in seconds
    private static final int     UPDATE_INTERVAL_IN_SECONDS   = 60 * UPDATE_INTERVAL_IN_MINUTES;
    // Update frequency in milliseconds
    private static final long   UPDATE_INTERVAL               = 1000 * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int    FASTEST_INTERVAL_IN_SECONDS   = 1;
    // A fast frequency ceiling in milliseconds
    private static final long   FASTEST_INTERVAL              = 1000 * FASTEST_INTERVAL_IN_SECONDS;
    // Update distance in meters
    private static final int    UPDATE_DISTANCE_IN_METERS     = 10;

    public interface ILocationListener {
        public void onLocationChanged(Location location);
    }

    private Location location;
    private LocationManager locationManager;

    private final List<ILocationListener> listeners = new ArrayList<ILocationListener>();

    public static WLocationManager get(Activity activity) {
        return WApplication.get(activity).locationManager();
    }

    public static WLocationManager get(Context context) {
        return WApplication.get(context).locationManager();
    }

    public WLocationManager(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        onLocationChanged(location);
    }

    public void addLocationListener(ILocationListener listener) {
        listeners.add(listener);
    }

    public void delLocationListener(ILocationListener listener) {
        listeners.remove(listener);
    }

    public void startUpdateLocation() {
        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            String provider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(provider, UPDATE_INTERVAL, UPDATE_DISTANCE_IN_METERS, this);
            Location knownLocation = locationManager.getLastKnownLocation(provider);
            onLocationChanged(knownLocation);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    public void stopUpdateLocation() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.location = location;
            for (ILocationListener listener : listeners) {
                listener.onLocationChanged(location);
            }
        }
        Log.i(LOG_TAG, "onLocationChanged:" + location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(LOG_TAG, "onStatusChanged:" + provider + ":" + status + ":" + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(LOG_TAG, "onProviderEnabled:" + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(LOG_TAG, "onProviderDisabled:" + provider);
    }

}
