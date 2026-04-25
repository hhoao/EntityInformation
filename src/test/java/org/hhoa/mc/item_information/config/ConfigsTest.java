package org.hhoa.mc.item_information.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ConfigsTest {
    @AfterEach
    void resetCompatibilityFlags() {
        Configs.syncFromConfig();
    }

    @Test
    void exposesDefaultCompatibilityFlagsBeforeConfigLoads() {
        assertTrue(Configs.enableItemToolTip);
        assertTrue(Configs.useWiki);
    }

    @Test
    void applyCompatibilityValuesUpdatesMutableRuntimeFlags() {
        Configs.applyCompatibilityValues(false, true);

        assertFalse(Configs.enableItemToolTip);
        assertTrue(Configs.useWiki);
    }

    @Test
    void syncFromConfigUsesDefaultsBeforeConfigLoads() {
        Configs.applyCompatibilityValues(false, false);

        Configs.syncFromConfig();

        assertTrue(Configs.enableItemToolTip);
        assertTrue(Configs.useWiki);
    }
}
