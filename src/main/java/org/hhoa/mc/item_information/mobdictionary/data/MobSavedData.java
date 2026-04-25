package org.hhoa.mc.item_information.mobdictionary.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

/**
 * MobData
 *
 * @author xianxing
 * @since 2024/10/29
 */
public class MobSavedData {
    private final Set<String> mobNameSet = new HashSet<>();
    private final String mobDataKey = "mob_data";

    public void load(CompoundTag tag) {
        ListTag mobData = (ListTag) tag.get(mobDataKey);

        if (mobData != null) {
            for (Tag mobDatum : mobData) {
                mobNameSet.add(mobDatum.getAsString());
            }
        }
    }

    public void save(@NotNull CompoundTag compoundTag) {
        ListTag tags = new ListTag();

        for (String name : mobNameSet) {
            StringTag nameTag = StringTag.valueOf(name);
            tags.add(nameTag);
        }
        compoundTag.put(mobDataKey, tags);
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
        return this.mobNameSet.contains(mobName);
    }

    public void removeMobName(String mobName) {
        mobNameSet.remove(mobName);
    }

    public void removeMobNames(Collection<String> mobNames) {
        mobNameSet.removeAll(mobNames);
    }
}
