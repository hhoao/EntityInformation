package org.hhoa.mc.item_information.mobdictionary.network;

import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.item.MobDataItem;
import org.hhoa.mc.item_information.utils.PlayerUtils;

/**
 * MobDictionaryGuiButtonClickEvent
 *
 * @author xianxing
 * @since 2024/11/3
 */
public class MobDictionaryGuiButtonClickEvent implements Event {
    private final String currentMobName;

    public MobDictionaryGuiButtonClickEvent(String currentMobName) {
        this.currentMobName = currentMobName;
    }

    public String getCurrentMobName() {
        return currentMobName;
    }

    @Override
    public EventType getRequestType() {
        return EventType.MOB_DICTIONARY_GUI_BUTTON_CLICK_EVENT;
    }

    public static MobDictionaryGuiButtonClickEvent decode(PacketBuffer buf) {
        String mobName = buf.readString();
        return new MobDictionaryGuiButtonClickEvent(mobName);
    }

    public static void encode(MobDictionaryGuiButtonClickEvent msg, PacketBuffer buf) {
        buf.writeString(msg.currentMobName);
    }

    public static void handle(
            MobDictionaryGuiButtonClickEvent message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get()
                .enqueueWork(
                        () -> {
                            ServerPlayerEntity player = ctx.get().getSender();
                            String mobName = message.getCurrentMobName();
                            if (player != null
                                    && MobDatas.containsMobNameOnServer(mobName, player)) {
                                ItemStack mobDataItemStack =
                                        MobDictionary.mobData.getDefaultInstance();
                                ItemStack paper = new ItemStack(Items.PAPER, 1);
                                ItemStack feather = new ItemStack(Items.FEATHER, 1);
                                if (PlayerUtils.hasItemCount(player, paper)
                                        && PlayerUtils.hasItemCount(player, feather)) {
                                    PlayerUtils.removeSingleItemFromPlayer(player, paper);
                                    PlayerUtils.removeSingleItemFromPlayer(player, feather);
                                    CompoundNBT tag =
                                            mobDataItemStack.getTag() == null
                                                    ? new CompoundNBT()
                                                    : mobDataItemStack.getTag();
                                    MobDataItem.setEntityNameToNBT(mobName, tag);
                                    mobDataItemStack.setTag(tag);

                                    MobDatas.removeMobNameOnServer(
                                            player, Collections.singleton(mobName));
                                    PlayerUtils.fireItemToPlayer(mobDataItemStack, player);
                                }
                            }
                        });

        ctx.get().setPacketHandled(true);
    }
}
