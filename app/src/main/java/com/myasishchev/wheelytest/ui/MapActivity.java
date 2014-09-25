package com.myasishchev.wheelytest.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.myasishchev.wheelytest.R;
import com.myasishchev.wheelytest.model.WLocationManager;
import com.myasishchev.wheelytest.model.WMarker;
import com.myasishchev.wheelytest.model.WSocketManager;
import com.myasishchev.wheelytest.util.BundleUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends ActionBarActivity implements WLocationManager.ILocationListener, WSocketManager.IMessagesListener {

    private static final String LOG_TAG = MapActivity.class.getSimpleName();
    private static final float DEFAULT_MAP_ZOOM = 11.0f;
    private static final String KEY_MARKERS = "markers";

    private Marker location;
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.

    private WSocketManager socketManager;
    private WLocationManager locationManager;

    private SparseArray<Marker> gmMarkers = new SparseArray<Marker>();
    private SparseArray<WMarker> wMarkers = new SparseArray<WMarker>();

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
        socketManager.addMessagesListener(this);
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.delLocationListener(this);
        socketManager.delMessagesListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gmMarkers.clear();
        wMarkers.clear();
        locationManager.stopUpdateLocation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        socketManager.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<WMarker> sMarkers = new ArrayList<WMarker>();
        for (int i = 0; i < wMarkers.size(); i++) {
            sMarkers.add(wMarkers.valueAt(i));
        }
        BundleUtils.putArguments(outState, KEY_MARKERS, sMarkers);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<WMarker> wMarkers = BundleUtils.getArguments(savedInstanceState, KEY_MARKERS, WMarker.class);
        for (WMarker wMarker: wMarkers) {
            addMapMarker(wMarker);
        }
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

    @Override
    public void onTextMessage(String payload) {
        try {
            JSONArray jsonArray = new JSONArray(payload);
            for (int i = 0; i < jsonArray.length(); i++) {
                WMarker wMarker = WMarker.create(jsonArray.getJSONObject(i));
                addMapMarker(wMarker);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private void addMapMarker(WMarker wMarker) {
        Marker marker = gmMarkers.get(wMarker.getId());
        if (marker == null) {
            marker = googleMap.addMarker(new MarkerOptions().position(wMarker.getPosition()));
            gmMarkers.put(wMarker.getId(), marker);
        } else {
            marker.setPosition(wMarker.getPosition());
        }
        wMarkers.put(wMarker.getId(), wMarker);
    }
}
