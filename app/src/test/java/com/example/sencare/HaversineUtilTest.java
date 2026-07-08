package com.example.sencare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.sencare.models.Spa;
import com.example.sencare.utils.HaversineUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HaversineUtilTest {

    @Test
    public void testFindSpasWithinDistance() {
        List<Spa> allSpas = new ArrayList<>();

        Spa spa1 = new Spa();
        spa1.setSpaId("1");
        spa1.setSpaName("Spa 1");
        spa1.setLatitude(10.0);
        spa1.setLongitude(10.0);

        Spa spa2 = new Spa();
        spa2.setSpaId("2");
        spa2.setSpaName("Spa 2");
        spa2.setLatitude(10.1); // approx 11km away
        spa2.setLongitude(10.0);

        allSpas.add(spa1);
        allSpas.add(spa2);

        // User is at (10.0, 10.0), search radius 5km
        // Spa 1 is at 0km, so it should be found within 5km.

        List<HaversineUtil.SpaResult> results = HaversineUtil.findSpasWithinDistance(10.0, 10.0, allSpas, 5.0);

        assertEquals(1, results.size());
        assertEquals("1", results.get(0).id);
    }

    @Test
    public void testAutoIncrementRadius() {
        List<Spa> allSpas = new ArrayList<>();

        Spa spa1 = new Spa();
        spa1.setSpaId("1");
        spa1.setSpaName("Far Spa");
        spa1.setLatitude(10.2); // approx 22km away
        spa1.setLongitude(10.0);

        allSpas.add(spa1);

        // Search radius 5km, should not find anything, then increment to 10, 15, 20, 25...
        // and find the spa at 25km.

        List<HaversineUtil.SpaResult> results = HaversineUtil.findSpasWithinDistance(10.0, 10.0, allSpas, 5.0);

        assertFalse(results.isEmpty());
        assertEquals("1", results.get(0).id);
        assertTrue(results.get(0).distance > 20.0);
    }
}
