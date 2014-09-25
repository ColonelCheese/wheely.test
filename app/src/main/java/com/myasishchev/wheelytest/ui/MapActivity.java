package com.myasishchev.wheelytest.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.myasishchev.wheelytest.R;
import com.myasishchev.wheelytest.model.WLocationManager;
import com.myasishchev.wheelytest.model.WSocketManager;

public class MapActivity extends ActionBarActivity implements WLocationManager.ILocationListener {

    private static final String LOG_TAG = MapActivity.class.getSimpleName();
    private static final float DEFAULT_MAP_ZOOM = 15.0f;

    private Marker location;
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.

    private WLocationManager locationManager;
    private WSocketManager socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        socketManager = WSocketManager.get(this);

        locationManager = WLocationManager.get(this);
        locationManager.startUpdateLocation();

        setUpMapIfNeeded();
    }

    private SupportMapFragment getMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment fragmentMap = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (fragmentMap == null) {
            fragmentMap = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.map, fragmentMap).commit();
        }
        return fragmentMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.addLocationListener(this);
        //WSocketManager.get(this).addPointsUpdateListener(this);
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.delLocationListener(this);
        //WSocketManager.get(this).delPointsUpdateListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.stopUpdateLocation();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = getMapFragment().getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        onLocationChanged(locationManager.getLocation());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            socketManager.sendLocation(location);
            setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    protected void setLocation(LatLng latLng) {
        if (location == null) {
            location = googleMap.addMarker(new MarkerOptions().position(latLng));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM);
            googleMap.animateCamera(cameraUpdate);
        } else {
            location.setPosition(latLng);
        }
    }

}
