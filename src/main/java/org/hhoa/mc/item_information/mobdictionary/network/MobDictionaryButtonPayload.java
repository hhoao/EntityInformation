package org.hhoa.mc.item_information.mobdictionary.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.hhoa.mc.item_information.ModInfo;

public record MobDictionaryButtonPayload(String currentMobName) implements CustomPacketPayload {
    public static final Type<MobDictionaryButtonPayload> TYPE =
            new Type<>(ModInfo.location("mobdictionary/gui_button"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MobDictionaryButtonPayload>
            STREAM_CODEC =
                    StreamCodec.composite(
                            ByteBufCodecs.STRING_UTF8,
                            MobDictionaryButtonPayload::currentMobName,
                            MobDictionaryButtonPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
