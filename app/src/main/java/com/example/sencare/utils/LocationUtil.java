package com.example.sencare.utils;

import android.location.Location;

public class LocationUtil {

    public static double calculateDistanceKm(
            double startLat,
            double startLng,
            double endLat,
            double endLng
    ) {
        float[] results = new float[1];

        Location.distanceBetween(
                startLat,
                startLng,
                endLat,
                endLng,
                results
        );

        return results[0] / 1000.0;
    }
}