package org.hhoa.mc.item_information.mobdictionary.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class MobDictionaryPayloads {
    private static final String PROTOCOL_VERSION = "1";

    private MobDictionaryPayloads() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playBidirectional(
                SyncMobDataPayload.TYPE,
                SyncMobDataPayload.STREAM_CODEC,
                MobDictionaryPayloadHandlers::handleSyncMobDataOnServer,
                MobDictionaryPayloadHandlers::handleSyncMobDataOnClient);
        registrar.playToServer(
                MobDictionaryButtonPayload.TYPE,
                MobDictionaryButtonPayload.STREAM_CODEC,
                MobDictionaryPayloadHandlers::handleMobDictionaryButton);
        registrar.playToServer(
                RegisterMobPayload.TYPE,
                RegisterMobPayload.STREAM_CODEC,
                MobDictionaryPayloadHandlers::handleRegisterMob);
    }
}
