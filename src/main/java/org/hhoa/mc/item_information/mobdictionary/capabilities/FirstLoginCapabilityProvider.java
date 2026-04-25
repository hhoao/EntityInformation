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

public class FirstLoginCapabilityProvider
        implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    private final IFirstLoginCapability instance = new FirstLoginCapabilityImpl();
    private final LazyOptional<IFirstLoginCapability> optional = LazyOptional.of(() -> instance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == MobDictionaryForgeEventsHandler.FIRST_LOGIN_CAPABILITY
                ? optional.cast()
                : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("HasLoggedIn", instance.hasLoggedIn());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.setHasLoggedIn(nbt.getBoolean("HasLoggedIn"));
    }
}
