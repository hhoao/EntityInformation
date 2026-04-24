package org.hhoa.mc.item_information.mobdictionary.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.junit.jupiter.api.Test;

class PayloadCodecTest {
    @Test
    void syncMobDataPayloadRoundTripsCodecData() {
        SyncMobDataPayload payload =
                new SyncMobDataPayload(List.of("entity.minecraft.zombie", "entity.minecraft.creeper"), EventType.PUT);

        SyncMobDataPayload decoded =
                roundTrip(SyncMobDataPayload.STREAM_CODEC, payload);

        assertIterableEquals(payload.mobNames(), decoded.mobNames());
        assertEquals(payload.requestType(), decoded.requestType());
    }

    @Test
    void mobDictionaryButtonPayloadRoundTripsCodecData() {
        MobDictionaryButtonPayload payload = new MobDictionaryButtonPayload("entity.minecraft.zombie");

        MobDictionaryButtonPayload decoded =
                roundTrip(MobDictionaryButtonPayload.STREAM_CODEC, payload);

        assertEquals(payload.currentMobName(), decoded.currentMobName());
    }

    @Test
    void registerMobPayloadRoundTripsCodecData() {
        RegisterMobPayload payload = new RegisterMobPayload("entity.minecraft.skeleton");

        RegisterMobPayload decoded = roundTrip(RegisterMobPayload.STREAM_CODEC, payload);

        assertEquals(payload.mobName(), decoded.mobName());
    }

    private static <T> T roundTrip(
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec, T payload) {
        RegistryFriendlyByteBuf buffer =
                new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        codec.encode(buffer, payload);
        buffer.readerIndex(0);
        return codec.decode(buffer);
    }
}
