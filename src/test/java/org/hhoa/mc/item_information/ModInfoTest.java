package org.hhoa.mc.item_information;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class ModInfoTest {
    @Test
    void exposesStableModIdentity() {
        assertEquals("entity_information", ModInfo.ID);
        assertEquals("entity_information:dictionary", ModInfo.location("dictionary").toString());
        assertFalse(ModInfo.location("dictionary").equals(ModInfo.location("data")));
    }
}
