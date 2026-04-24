package org.hhoa.mc.item_information.mobdictionary.network;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.hhoa.mc.item_information.ModInfo;

public record SyncMobDataPayload(List<String> mobNames, EventType requestType)
        implements CustomPacketPayload, Event {
    public static final Type<SyncMobDataPayload> TYPE =
            new Type<>(ModInfo.location("mobdictionary/sync_mob_data"));
    private static final StreamCodec<ByteBuf, EventType> EVENT_TYPE_STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(EventType::valueOf, EventType::name);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncMobDataPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
                    SyncMobDataPayload::mobNames,
                    EVENT_TYPE_STREAM_CODEC,
                    SyncMobDataPayload::requestType,
                    SyncMobDataPayload::new);

    public SyncMobDataPayload {
        mobNames = List.copyOf(mobNames);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public EventType getRequestType() {
        return requestType;
    }
}
