package org.hhoa.mc.item_information.mobdictionary.network.packet.register;

import java.io.IOException;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;

public class RegisterMobMessageHandler {
    public static void handle(RegisterMobMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get()
                .enqueueWork(
                        () -> {
                            ServerPlayer player = ctx.get().getSender();
                            if (player != null
                                    && player.getUUID()
                                            .toString()
                                            .equals(message.getUUIDString())) {
                                try {
                                    MobDatas.saveMobNameOnServer(player, message.getMobName());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

        ctx.get().setPacketHandled(true);
    }
}
