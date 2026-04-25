package org.hhoa.mc.item_information.mobdictionary.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.item.MobDataItem;
import org.hhoa.mc.item_information.mobdictionary.messages.ChatText;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.hhoa.mc.item_information.utils.PlayerUtils;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class MobDictionaryPayloadHandlers {
    private MobDictionaryPayloadHandlers() {}

    public static void handleSyncMobDataOnClient(
            SyncMobDataPayload payload, IPayloadContext context) {
        ArrayList<String> successMobNames =
                switch (payload.requestType()) {
                    case PUT -> MobDatas.addMobNamesOnClient(payload.mobNames());
                    case DELETE -> MobDatas.removeMobNamesOnClient(payload.mobNames());
                    case REGISTER -> {
                        MobDatas.replaceClientMobNames(payload.mobNames());
                        yield new ArrayList<>(payload.mobNames());
                    }
                    default -> new ArrayList<>();
                };

        if (payload.requestType() != EventType.REGISTER
                && context.player() instanceof LocalPlayer player
                && !successMobNames.isEmpty()) {
            ChatText chatMessage =
                    payload.requestType() == EventType.PUT ? Texts.ACCEPT : Texts.UNREGISTER;
            player.displayClientMessage(
                    chatMessage
                            .withTranslatableTexts(payload.mobNames().toArray(new String[0]))
                            .getTextComponent(),
                    false);
        }

        MobDictionary.getDispatcher().process(payload);
    }

    public static void handleSyncMobDataOnServer(
            SyncMobDataPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        switch (payload.requestType()) {
            case DELETE -> MobDatas.removeMobNameOnServer(player, payload.mobNames());
            case PUT -> MobDatas.saveMobNamesOnServer(player, payload.mobNames());
            default -> {}
        }
    }

    public static void handleRegisterMob(RegisterMobPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        try {
            MobDatas.saveMobNameOnServer(player, payload.mobName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to register mob payload for " + payload.mobName(), e);
        }
    }

    public static void handleMobDictionaryButton(
            MobDictionaryButtonPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)
                || !MobDatas.containsMobNameOnServer(payload.currentMobName(), player)) {
            return;
        }

        ItemStack paper = new ItemStack(Items.PAPER, 1);
        ItemStack feather = new ItemStack(Items.FEATHER, 1);
        if (!PlayerUtils.hasItemCount(player, paper) || !PlayerUtils.hasItemCount(player, feather)) {
            return;
        }

        PlayerUtils.removeSingleItemFromPlayer(player, paper);
        PlayerUtils.removeSingleItemFromPlayer(player, feather);

        ItemStack mobDataItemStack = MobDictionary.mobData.get().getDefaultInstance();
        CompoundTag tag = new CompoundTag();
        MobDataItem.setEntityNameToNBT(payload.currentMobName(), tag);
        mobDataItemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        MobDatas.removeMobNameOnServer(player, Collections.singleton(payload.currentMobName()));
        PlayerUtils.fireItemToPlayer(mobDataItemStack, player);
    }
}
