package com.example.a16adventure;

import com.example.a16adventure.models.Monument;
import com.example.a16adventure.models.MonumentDataManager;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MonumentDataManagerTest {
    @Test
    public void getMonumentById_shouldReturnExpectedMonument() {
        MonumentDataManager manager = MonumentDataManager.getInstance();
        Monument m1 = new Monument("1", "A", "D1", "", "", 0, 0);
        Monument m2 = new Monument("2", "B", "D2", "", "", 0, 0);
        manager.setMonumentList(Arrays.asList(m1, m2));

        Monument result = manager.getMonumentById("2");
        assertNotNull(result);
        assertEquals("B", result.getName());
    }

    @Test
    public void getMonumentById_shouldReturnNullWhenMissing() {
        MonumentDataManager manager = MonumentDataManager.getInstance();
        manager.setMonumentList(Arrays.asList(new Monument("1", "A", "D1", "", "", 0, 0)));

        assertNull(manager.getMonumentById("404"));
    }
}
