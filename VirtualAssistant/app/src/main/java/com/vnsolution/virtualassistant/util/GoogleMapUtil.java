package com.vnsolution.virtualassistant.util;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vnsolution.virtualassistant.R;

public class GoogleMapUtil {
    public static void drawCircle(GoogleMap googleMap, LatLng latLng, float zoom) {
        if (googleMap != null) {
            final CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.fillColor(Color.RED);
            circleOptions.strokeWidth(1);
            circleOptions.strokeColor(Color.RED);
            double radius = calculateCircleRadiusMeterForMapCircle(12, latLng.latitude, zoom);
            circleOptions.radius(radius);
            googleMap.addCircle(circleOptions);
        }
    }

    public static void drawMarker(GoogleMap googleMap, LatLng latLng) {
        drawMarker(googleMap, latLng, null, 0,false,0);
    }

    public static void drawMarker(GoogleMap googleMap, LatLng latLng, String title) {
        drawMarker(googleMap, latLng, title, 0,false,0);
    }

    public static void drawMarker(GoogleMap googleMap, LatLng latLng, int drawableId) {
        drawMarker(googleMap, latLng, null, drawableId,false,0);
    }

    public static void drawMarker(GoogleMap googleMap, LatLng latLng, String title, int drawableId) {
        drawMarker(googleMap, latLng, title, drawableId,false,0);
    }

    public static void drawMarker(GoogleMap googleMap, LatLng latLng, String title, boolean focused, float zoom) {
        drawMarker(googleMap, latLng, title, 0, focused, zoom);
    }

    public static void drawMarker(GoogleMap googleMap, LatLng latLng, String title, int drawableId, boolean focused, float zoom) {
        if (googleMap != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            // Draw circles of constant size on screen in Google Maps API v2
            // https://stackoverflow.com/questions/16421937/draw-circles-of-constant-size-on-screen-in-google-maps-api-v2
            markerOptions.anchor(0.5f, 0.5f);
            if (title != null && title != "") {
                markerOptions.title(title);
            }
            BitmapDescriptor bitmap = (drawableId > 0) ? BitmapDescriptorFactory.fromResource(drawableId) : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            markerOptions.icon(bitmap);
            googleMap.addMarker(markerOptions);
            if (focused) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            }
        }
    }

    private static double calculateCircleRadiusMeterForMapCircle(final int targetRadiusDip, final double circleCenterLatitude, final float currentMapZoom) {
        //That base value seems to work for computing the meter length of a DIP
        final double arbitraryValueForDip = 156000D;
        final double oneDipDistance = Math.abs(Math.cos(Math.toRadians(circleCenterLatitude))) * arbitraryValueForDip / Math.pow(2, currentMapZoom);
        return oneDipDistance * (double) targetRadiusDip;
    }
}
