package org.hhoa.mc.item_information.mobdictionary.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;

public class MobDataCapabilityProvider
        implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
    private final MobDataCapability instance = new MobDataCapabilityImpl();
    private final LazyOptional<MobDataCapability> optional = LazyOptional.of(() -> instance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == MobDictionary.mobDataCapability ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        instance.getMobSavedData().write(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        instance.getMobSavedData().read(nbt);
    }
}
