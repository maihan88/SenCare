package com.example.sencare.utils;

import com.example.sencare.models.Spa;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HaversineUtil {

    public static class SpaResult {
        public String id;
        public double distance;
        public Spa spa;

        public SpaResult(Spa spa, double distance) {
            this.id = spa.getSpaId();
            this.spa = spa;
            this.distance = distance;
        }
    }

    /**
     * Finds spas within a certain radius using the Haversine distance. If no spas are found,
     * it increments the radius by 5km until at least one spa is found.
     */
    public static List<SpaResult> findSpasWithinDistance(double userLat, double userLng, List<Spa> allSpas, double initialRadius) {
        double currentRadius = initialRadius;
        List<SpaResult> result = new ArrayList<>();

        while (result.isEmpty()) {
            result = getSpasInRadius(userLat, userLng, allSpas, currentRadius);
            if (!result.isEmpty()) break;
            currentRadius += 5.0; // Increment by 5km

            // Safety break to prevent infinite loop if no spas exist in DB
            if (currentRadius > 1000) break;
        }

        // Sort by distance (nearest first)
        Collections.sort(result, Comparator.comparingDouble(n -> n.distance));

        return result;
    }

    private static List<SpaResult> getSpasInRadius(double userLat, double userLng, List<Spa> allSpas, double radius) {
        List<SpaResult> found = new ArrayList<>();

        // Calculate the Haversine distance from the user to each spa and keep the ones within radius
        for (Spa spa : allSpas) {
            double dist = LocationUtil.calculateDistanceKm(userLat, userLng, spa.getLatitude(), spa.getLongitude());
            if (dist <= radius) {
                found.add(new SpaResult(spa, dist));
            }
        }
        return found;
    }
}
