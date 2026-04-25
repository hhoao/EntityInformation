package org.hhoa.mc.item_information.mobdictionary.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class MobDataCapabilityStorage implements Capability.IStorage<MobDataCapability> {
    @Override
    public INBT writeNBT(
            Capability<MobDataCapability> capability, MobDataCapability instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        instance.getMobSavedData().write(tag);
        return tag;
    }

    @Override
    public void readNBT(
            Capability<MobDataCapability> capability,
            MobDataCapability instance,
            Direction side,
            INBT nbt) {
        if (nbt instanceof CompoundNBT) {
            instance.getMobSavedData().read((CompoundNBT) nbt);
        }
    }
}
