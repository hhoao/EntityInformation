package org.hhoa.mc.item_information.mobdictionary.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.hhoa.mc.item_information.ModInfo;

public record RegisterMobPayload(String mobName) implements CustomPacketPayload {
    public static final Type<RegisterMobPayload> TYPE =
            new Type<>(ModInfo.location("mobdictionary/register_mob"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RegisterMobPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    RegisterMobPayload::mobName,
                    RegisterMobPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
