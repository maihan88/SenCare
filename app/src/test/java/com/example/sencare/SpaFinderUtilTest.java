package com.example.sencare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.sencare.models.Spa;
import com.example.sencare.models.SpaResult;
import com.example.sencare.utils.SpaFinderUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SpaFinderUtilTest {

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
        spa2.setLatitude(10.1); // cách khoảng 11km
        spa2.setLongitude(10.0);

        allSpas.add(spa1);
        allSpas.add(spa2);

        // User ở (10.0, 10.0), bán kính 5km
        // Spa 1 cách 0km nên nằm trong 5km, Spa 2 (~11km) bị loại
        List<SpaResult> results = SpaFinderUtil.findSpasWithinDistance(10.0, 10.0, allSpas, 5.0);

        assertEquals(1, results.size());
        assertEquals("1", results.get(0).spa.getSpaId());
    }

    @Test
    public void testAutoIncrementRadius() {
        List<Spa> allSpas = new ArrayList<>();

        Spa spa1 = new Spa();
        spa1.setSpaId("1");
        spa1.setSpaName("Far Spa");
        spa1.setLatitude(10.2); // cách khoảng 22km
        spa1.setLongitude(10.0);

        allSpas.add(spa1);

        // Bán kính 5km không tìm thấy gì → tự nới 10, 15, 20, 25... và tìm được spa ở ~22km
        List<SpaResult> results = SpaFinderUtil.findSpasWithinDistance(10.0, 10.0, allSpas, 5.0);

        assertFalse(results.isEmpty());
        assertEquals("1", results.get(0).spa.getSpaId());
        assertTrue(results.get(0).distanceKm > 20.0);
    }
}
