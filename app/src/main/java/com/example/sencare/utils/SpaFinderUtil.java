package com.example.sencare.utils;

import com.example.sencare.models.Spa;
import com.example.sencare.models.SpaResult;
import java.util.ArrayList;
import java.util.List;

public class SpaFinderUtil {
    public static List<SpaResult> findSpasWithinDistance(double userLat, double userLng, List<Spa> allSpas, double initialRadius) {
        double currentRadius = initialRadius;
        List<SpaResult> result = new ArrayList<>();

        while (result.isEmpty()) {
            result = getSpasInRadius(userLat, userLng, allSpas, currentRadius);
            if (!result.isEmpty()) break;
            currentRadius += 1.0;

            if (currentRadius > 1000) break;
        }

        return result;
    }

    private static List<SpaResult> getSpasInRadius(double userLat, double userLng, List<Spa> allSpas, double radius) {
        List<SpaResult> found = new ArrayList<>();

        for (Spa spa : allSpas) {
            double dist = HaversineUtil.calculateDistanceKm(userLat, userLng, spa.getLatitude(), spa.getLongitude());
            if (dist <= radius) {
                found.add(new SpaResult(spa, dist));
            }
        }
        return found;
    }
}
