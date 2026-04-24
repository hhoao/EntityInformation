package org.hhoa.mc.item_information.mobdictionary.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

class MobSavedDataTest {
    @Test
    void codecRoundTripsUnlockedMobs() {
        MobSavedData data = new MobSavedData();
        data.addMobName("entity.minecraft.cow");
        data.addMobName("entity.minecraft.zombie");

        var encoded = MobSavedData.CODEC.codec().encodeStart(JsonOps.INSTANCE, data).getOrThrow();
        MobSavedData decoded = MobSavedData.CODEC.codec().parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(2, decoded.getMobNameSet().size());
        assertTrue(decoded.containsMobName("entity.minecraft.cow"));
        assertTrue(decoded.containsMobName("entity.minecraft.zombie"));
    }

    @Test
    void codecRoundTripsFirstLoginFlag() {
        MobSavedData data = new MobSavedData();
        data.setHasLoggedIn(true);

        var encoded = MobSavedData.CODEC.codec().encodeStart(JsonOps.INSTANCE, data).getOrThrow();
        MobSavedData decoded = MobSavedData.CODEC.codec().parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertTrue(decoded.hasLoggedIn());
    }
}
