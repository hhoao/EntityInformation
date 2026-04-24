package org.hhoa.mc.item_information.mobdictionary.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MobSavedData {
    public static final MapCodec<MobSavedData> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance ->
                            instance.group(
                                            Codec.STRING.listOf()
                                                    .optionalFieldOf("mob_names", List.of())
                                                    .forGetter(MobSavedData::toList),
                                            Codec.BOOL.optionalFieldOf("has_logged_in", false)
                                                    .forGetter(MobSavedData::hasLoggedIn))
                                    .apply(instance, MobSavedData::fromSerialized));

    private final Set<String> mobNameSet = new LinkedHashSet<>();
    private boolean hasLoggedIn;

    private static MobSavedData fromSerialized(List<String> mobNames, boolean hasLoggedIn) {
        MobSavedData data = new MobSavedData();
        data.addMobNames(mobNames);
        data.setHasLoggedIn(hasLoggedIn);
        return data;
    }

    public List<String> toList() {
        return List.copyOf(mobNameSet);
    }

    public Set<String> getMobNameSet() {
        return mobNameSet;
    }

    public void addMobName(String mobName) {
        mobNameSet.add(mobName);
    }

    public void addMobNames(Collection<String> mobNames) {
        mobNameSet.addAll(mobNames);
    }

    public HashSet<String> clearMobNames() {
        HashSet<String> mobNames = new HashSet<>(mobNameSet);
        mobNameSet.clear();
        return mobNames;
    }

    public boolean containsMobName(String mobName) {
        return mobNameSet.contains(mobName);
    }

    public void removeMobName(String mobName) {
        mobNameSet.remove(mobName);
    }

    public void removeMobNames(Collection<String> mobNames) {
        mobNameSet.removeAll(mobNames);
    }

    public boolean hasLoggedIn() {
        return hasLoggedIn;
    }

    public void setHasLoggedIn(boolean value) {
        hasLoggedIn = value;
    }
}
