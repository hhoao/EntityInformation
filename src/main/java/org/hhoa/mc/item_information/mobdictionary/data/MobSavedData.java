package org.hhoa.mc.item_information.mobdictionary.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

/**
 * MobData
 *
 * @author xianxing
 * @since 2024/10/29
 */
public class MobSavedData extends SavedData {
    private final Set<String> mobNameSet = new HashSet<>();

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        ListTag tags = new ListTag();

        for (String name : mobNameSet) {
            StringTag nameTag = StringTag.valueOf(name);
            tags.add(nameTag);
        }
        compoundTag.put("mob_data", tags);
        return compoundTag;
    }

    public static MobSavedData load(CompoundTag tag) {
        ListTag mobData = (ListTag) tag.get("mob_data");

        MobSavedData mobSavedData = new MobSavedData();
        if (mobData != null) {
            Set<String> mobNameList = mobSavedData.getMobNameSet();
            for (Tag mobDatum : mobData) {
                mobNameList.add(mobDatum.getAsString());
            }
        }
        return mobSavedData;
    }

    public Set<String> getMobNameSet() {
        return mobNameSet;
    }

    public void addMobName(String mobName) {
        mobNameSet.add(mobName);
        this.setDirty(true);
    }

    public void addMobNames(Collection<String> mobNames) {
        mobNameSet.addAll(mobNames);
        this.setDirty(true);
    }

    public HashSet<String> clearMobNames() {
        HashSet<String> mobNames = new HashSet<>(mobNameSet);
        mobNameSet.clear();
        this.setDirty(true);
        return mobNames;
    }

    public boolean containsMobName(String mobName) {
        return this.mobNameSet.contains(mobName);
    }

    public void removeMobName(String mobName) {
        mobNameSet.remove(mobName);
        this.setDirty(true);
    }

    public void removeMobNames(Collection<String> mobNames) {
        mobNameSet.removeAll(mobNames);
        this.setDirty(true);
    }
}
