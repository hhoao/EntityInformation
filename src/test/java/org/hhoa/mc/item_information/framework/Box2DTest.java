package org.hhoa.mc.item_information.framework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Box2DTest {
    @Test
    void acceptsNormalBounds() {
        Box2D box = assertDoesNotThrow(() -> new Box2D(4, 8, 12, 24));

        assertEquals(4, box.getMinX());
        assertEquals(8, box.getMinY());
        assertEquals(12, box.getMaxX());
        assertEquals(24, box.getMaxY());
        assertTrue(box.isInBox(8, 16));
    }
}
