package com.github.e2318501.personalchest;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BlockLocationTest {

    @Test
    void testEquals1() {
        BlockLocation bloc1 = new BlockLocation("world", 0, 0, 0);
        BlockLocation bloc2 = new BlockLocation("world", 0, 0, 0);

        assertEquals(bloc1, bloc2);
    }

    @Test
    void testEquals2() {
        BlockLocation bloc1 = new BlockLocation("world", 0, 0, 0);
        BlockLocation bloc2 = new BlockLocation("world", 0, 0, 0);

        Map<BlockLocation, Object> map = new HashMap<>();
        map.put(bloc1, new Object());
        assertTrue(map.containsKey(bloc2));
    }
}
