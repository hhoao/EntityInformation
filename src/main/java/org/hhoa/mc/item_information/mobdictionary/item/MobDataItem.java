package org.hhoa.mc.item_information.mobdictionary.item;

import java.util.List;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.hhoa.mc.item_information.utils.PlayerUtils;
import org.jetbrains.annotations.NotNull;

public class MobDataItem extends Item {
    public MobDataItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        CompoundTag nbt = itemStack.getTag();

        String name = "";
        try {
            if (nbt != null) {
                name = getEntityNameFromNBT(nbt);
                if (player instanceof ServerPlayer playerMP) {
                    if (!MobDatas.containsMobNameOnClient(name)) {
                        MobDatas.saveMobNameOnServer(playerMP, name);
                        PlayerUtils.removeSingleItemFromPlayer(player, itemStack.getItem(), 1);
                    } else {
                        player.sendMessage(
                                Texts.ALREADY.withTranslatableTexts(name).getTextComponent(),
                                player.getUUID());
                        return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
                    }
                }
            }
        } catch (Exception e) {
            player.sendMessage(
                    Texts.ERROR.withTranslatableTexts(name).getTextComponent(), player.getUUID());
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Level world,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag) {
        CompoundTag nbt = stack.getTag();
        StringBuilder sb;

        if (nbt != null) {
            String name = getEntityNameFromNBT(nbt);

            if (!name.isEmpty()) {
                sb = new StringBuilder().append(Texts.NAME).append(":").append(I18n.get(name));
                tooltip.add(new TextComponent(sb.toString()));
            }
        }
    }

    public static String getEntityNameFromNBT(CompoundTag nbt) {
        return nbt.getString("Name");
    }

    public static void setEntityNameToNBT(String name, CompoundTag nbt) {
        nbt.putString("Name", name);
    }

    @Override
    public void fillItemCategory(
            @NotNull CreativeModeTab tab, @NotNull NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            for (String name : MobDictionary.getEntityManager().getEntityNames()) {
                if (MobDictionary.getEntityManager().containsName(name)) {
                    ItemStack item = new ItemStack(this);
                    CompoundTag nbt = new CompoundTag();
                    setEntityNameToNBT(name, nbt);
                    item.setTag(nbt);
                    items.add(item);
                }
            }
        }
    }
}
