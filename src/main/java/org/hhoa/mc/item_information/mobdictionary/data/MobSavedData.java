package org.hhoa.mc.item_information.mobdictionary.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * MobData
 *
 * @author xianxing
 * @since 2024/10/29
 */
public class MobSavedData {
    private Set<String> mobNameSet = new HashSet<>();

    public @NotNull CompoundNBT write(@NotNull CompoundNBT compoundTag) {
        ListNBT tags = new ListNBT();

        for (String name : mobNameSet) {
            StringNBT nameTag = StringNBT.valueOf(name);
            tags.add(nameTag);
        }
        compoundTag.put("mob_data", tags);
        return compoundTag;
    }

    public void read(CompoundNBT tag) {
        ListNBT mobData = (ListNBT) tag.get("mob_data");

        MobSavedData mobSavedData = new MobSavedData();
        if (mobData != null) {
            Set<String> mobNameList = mobSavedData.getMobNameSet();
            for (INBT mobDatum : mobData) {
                mobNameList.add(mobDatum.getString());
            }
            this.mobNameSet = mobNameList;
        }
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
