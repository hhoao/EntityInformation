package org.hhoa.mc.item_information.mobdictionary.network;

import static org.hhoa.mc.item_information.EntityInformation.location;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.hhoa.mc.item_information.mobdictionary.network.packet.register.RegisterMobMessage;
import org.hhoa.mc.item_information.mobdictionary.network.packet.register.RegisterMobMessageHandler;
import org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata.ClientSyncDataMessage;
import org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata.ClientSyncDataMessageHandler;
import org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata.ServerSyncDataMessage;
import org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata.ServerSyncDataMessageHandler;
import org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata.SyncDataMessage;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel CHANNEL =
            NetworkRegistry.ChannelBuilder.named(location("general"))
                    .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                    .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                    .networkProtocolVersion(() -> PROTOCOL_VERSION)
                    .simpleChannel();

    public static void registerMessages() {
        int messageNumber = 1;
        CHANNEL.messageBuilder(
                        RegisterMobMessage.class, messageNumber++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RegisterMobMessage::encode)
                .decoder(RegisterMobMessage::decode)
                .consumerNetworkThread(RegisterMobMessageHandler::handle)
                .add();
        CHANNEL.messageBuilder(
                        ClientSyncDataMessage.class,
                        messageNumber++,
                        NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClientSyncDataMessage::encode)
                .decoder((buf) -> (ClientSyncDataMessage) SyncDataMessage.decode(buf, false))
                .consumerNetworkThread(ClientSyncDataMessageHandler::handle)
                .add();
        CHANNEL.messageBuilder(
                        ServerSyncDataMessage.class,
                        messageNumber++,
                        NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ServerSyncDataMessage::encode)
                .decoder((buf) -> (ServerSyncDataMessage) SyncDataMessage.decode(buf, true))
                .consumerNetworkThread(ServerSyncDataMessageHandler::handle)
                .add();
        CHANNEL.messageBuilder(
                        MobDictionaryGuiButtonClickEvent.class,
                        messageNumber++,
                        NetworkDirection.PLAY_TO_SERVER)
                .encoder(MobDictionaryGuiButtonClickEvent::encode)
                .decoder(MobDictionaryGuiButtonClickEvent::decode)
                .consumerNetworkThread(MobDictionaryGuiButtonClickEvent::handle)
                .add();
    }

    public static void init() {
        PacketHandler.registerMessages();
    }
}
