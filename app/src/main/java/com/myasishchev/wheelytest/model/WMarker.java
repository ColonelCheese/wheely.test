package com.myasishchev.wheelytest.model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.Serializable;

public class WMarker implements Serializable {

    private static final java.lang.String TAG_ID = "id";
    private static final java.lang.String TAG_LATITUDE = "lat";
    private static final java.lang.String TAG_LONGITUDE = "lon";

    private int id;
    private double latitude;
    private double longitude;

    private WMarker(JSONObject jsonObject) {
        id = jsonObject.optInt(TAG_ID, 0);
        latitude = jsonObject.optDouble(TAG_LATITUDE, 0);
        longitude = jsonObject.optDouble(TAG_LONGITUDE, 0);
    }

    public static WMarker create(JSONObject jsonObject) {
        return new WMarker(jsonObject);
    }

    public int getId() {
        return id;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WMarker && ((WMarker) o).getId() == getId();
    }

}
