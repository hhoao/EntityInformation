package org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.ChatText;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.hhoa.mc.item_information.mobdictionary.network.Dispatcher;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;

public class ServerSyncDataMessageHandler {
    public static void handle(SyncDataMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get()
                .enqueueWork(
                        () -> {
                            ClientPlayerEntity player = Minecraft.getInstance().player;
                            Collection<String> nameList = message.getNameList();
                            EventType requestType = message.getRequestType();
                            ArrayList<String> successMobNames = null;
                            switch (requestType) {
                                case PUT:
                                case REGISTER:
                                    successMobNames = MobDatas.addMobNamesOnClient(nameList);
                                    break;
                                case DELETE:
                                    {
                                        successMobNames = MobDatas.removeMobNamesOnClient(nameList);
                                    }
                            }

                            if (requestType != EventType.REGISTER) {
                                ChatText chatMessage =
                                        requestType == EventType.PUT
                                                ? Texts.ACCEPT
                                                : Texts.UNREGISTER;
                                if (player != null
                                        && successMobNames != null
                                        && !successMobNames.isEmpty()) {
                                    player.sendMessage(
                                            chatMessage
                                                    .withTranslatableTexts(
                                                            nameList.toArray(new String[0]))
                                                    .getTextComponent(),
                                            player.getUniqueID());
                                }
                            }
                            Dispatcher dispatcher = MobDictionary.getDispatcher();
                            dispatcher.process(message);
                        });

        ctx.get().setPacketHandled(true);
    }
}
