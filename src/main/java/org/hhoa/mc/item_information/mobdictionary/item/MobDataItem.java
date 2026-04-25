package org.hhoa.mc.item_information.mobdictionary.item;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.hhoa.mc.item_information.utils.PlayerUtils;
import org.jetbrains.annotations.NotNull;

public class MobDataItem extends Item {
    public MobDataItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(
            @NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        String name = "";
        try {
            if (hasEntityName(itemStack)) {
                name = getEntityName(itemStack);
                if (player instanceof ServerPlayer playerMP && !name.isEmpty()) {
                    if (!MobDatas.containsMobNameOnServer(name, playerMP)) {
                        MobDatas.saveMobNameOnServer(playerMP, name);
                        PlayerUtils.removeSingleItemFromPlayer(player, itemStack.getItem(), 1);
                    } else {
                        player.displayClientMessage(
                                Texts.ALREADY.withTranslatableTexts(name).getTextComponent(), false);
                        return InteractionResult.FAIL;
                    }
                }
            }
        } catch (Exception e) {
            player.displayClientMessage(Texts.ERROR.withTranslatableTexts(name).getTextComponent(), false);
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay tooltipDisplay,
            @NotNull Consumer<Component> tooltip,
            @NotNull TooltipFlag flag) {
        if (hasEntityName(stack)) {
            String name = getEntityName(stack);
            StringBuilder sb = new StringBuilder().append(Texts.NAME).append(":").append(I18n.get(name));
            tooltip.accept(Component.literal(sb.toString()));
        }
    }

    public static String getEntityName(ItemStack stack) {
        return getEntityNameFromNBT(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
    }

    public static boolean hasEntityName(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains("Name");
    }

    public static void setEntityName(ItemStack stack, String name) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> setEntityNameToNBT(name, tag));
    }

    public static String getEntityNameFromNBT(CompoundTag nbt) {
        return nbt.getStringOr("Name", "");
    }

    public static void setEntityNameToNBT(String name, CompoundTag nbt) {
        nbt.putString("Name", name);
    }
}
