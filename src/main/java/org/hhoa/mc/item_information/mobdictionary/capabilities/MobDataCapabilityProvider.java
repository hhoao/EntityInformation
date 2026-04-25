package org.hhoa.mc.item_information.mobdictionary.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.hhoa.mc.item_information.mobdictionary.MobDictionaryForgeEventsHandler;

public class MobDataCapabilityProvider
        implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    private final MobDataCapability instance = new MobDataCapabilityImpl();
    private final LazyOptional<MobDataCapability> optional = LazyOptional.of(() -> instance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == MobDictionaryForgeEventsHandler.MOB_DATA_CAPABILITY
                ? optional.cast()
                : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundNBT = new CompoundTag();
        instance.getMobSavedData().save(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.getMobSavedData().load(nbt);
    }
}
