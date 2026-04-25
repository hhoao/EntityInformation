package org.hhoa.mc.item_information.mobdictionary.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class FirstLoginCapabilityStorage implements Capability.IStorage<IFirstLoginCapability> {
    @Override
    public INBT writeNBT(
            Capability<IFirstLoginCapability> capability,
            IFirstLoginCapability instance,
            Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("firstLogin", instance.hasLoggedIn());
        return tag;
    }

    @Override
    public void readNBT(
            Capability<IFirstLoginCapability> capability,
            IFirstLoginCapability instance,
            Direction side,
            INBT nbt) {
        if (nbt instanceof CompoundNBT) {
            CompoundNBT tag = (CompoundNBT) nbt;
            instance.setHasLoggedIn(tag.getBoolean("firstLogin"));
        }
    }
}
