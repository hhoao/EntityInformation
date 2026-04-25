package org.hhoa.mc.item_information.mobdictionary.network;

import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;
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

    public static MobDictionaryGuiButtonClickEvent decode(FriendlyByteBuf buf) {
        String mobName = buf.readUtf();
        return new MobDictionaryGuiButtonClickEvent(mobName);
    }

    public static void encode(MobDictionaryGuiButtonClickEvent msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.currentMobName);
    }

    public static void handle(
            MobDictionaryGuiButtonClickEvent message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get()
                .enqueueWork(
                        () -> {
                            ServerPlayer player = ctx.get().getSender();
                            String mobName = message.getCurrentMobName();
                            if (player != null
                                    && MobDatas.containsMobNameOnServer(mobName, player)) {
                                ItemStack mobDataItemStack =
                                        MobDictionary.mobData.get().getDefaultInstance();
                                ItemStack paper = new ItemStack(Items.PAPER, 1);
                                ItemStack feather = new ItemStack(Items.FEATHER, 1);
                                if (PlayerUtils.hasItemCount(player, paper)
                                        && PlayerUtils.hasItemCount(player, feather)) {
                                    PlayerUtils.removeSingleItemFromPlayer(player, paper);
                                    PlayerUtils.removeSingleItemFromPlayer(player, feather);
                                    CompoundTag tag =
                                            mobDataItemStack.getTag() == null
                                                    ? new CompoundTag()
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
