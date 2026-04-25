package org.hhoa.mc.item_information.mobdictionary.network.packet.syncdata;

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.network.EventType;

public class ClientSyncDataMessageHandler {
    public static void handle(SyncDataMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get()
                .enqueueWork(
                        () -> {
                            ServerPlayerEntity sender = ctx.get().getSender();
                            if (message.getRequestType() == EventType.DELETE) {
                                MobDatas.removeMobNameOnServer(sender, message.getNameList());
                            } else if (message.getRequestType() == EventType.PUT) {
                                MobDatas.saveMobNamesOnServer(sender, message.getNameList());
                            }
                        });

        ctx.get().setPacketHandled(true);
    }
}
