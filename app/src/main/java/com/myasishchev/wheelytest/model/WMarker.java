package com.myasishchev.wheelytest.model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by MyasishchevA on 25.09.2014.
 */
public class WMarker implements Serializable {

    private static final java.lang.String TAG_ID = "id";
    private static final java.lang.String TAG_LATITUDE = "lat";
    private static final java.lang.String TAG_LONGITUDE = "lon";

    private int id;
    private LatLng position;

    private WMarker(JSONObject jsonObject) {
        id = jsonObject.optInt(TAG_ID, 0);
        double latitude = jsonObject.optDouble(TAG_LATITUDE, 0);
        double longitude = jsonObject.optDouble(TAG_LONGITUDE, 0);
        position = new LatLng(latitude, longitude);
    }

    public static WMarker create(JSONObject jsonObject) {
        return new WMarker(jsonObject);
    }

    public int getId() {
        return id;
    }

    public LatLng getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WMarker && ((WMarker) o).getId() == getId();
    }

}
