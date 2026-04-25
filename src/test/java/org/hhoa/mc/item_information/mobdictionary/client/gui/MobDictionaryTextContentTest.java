package org.hhoa.mc.item_information.mobdictionary.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MobDictionaryTextContentTest {
    @Test
    void formatsLockedDisplayNameWithUnknownPrefixAndId() {
        assertEquals(
                "Unknown Biology #7",
                MobDictionaryTextContent.lockedDisplayName("Unknown Biology", 7));
    }

    @Test
    void formatsRegisteredProgressCount() {
        assertEquals("12/48", MobDictionaryTextContent.progressText(12, 48));
    }
}
