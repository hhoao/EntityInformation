package org.hhoa.mc.item_information.mobdictionary.item;

import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.antlr.v4.runtime.misc.NotNull;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.hhoa.mc.item_information.utils.PlayerUtils;

public class MobDataItem extends Item {
    public MobDataItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(
            @NotNull World world, PlayerEntity player, @NotNull Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        CompoundNBT nbt = itemStack.getTag();

        String name = "";
        try {
            if (nbt != null) {
                name = getEntityNameFromNBT(nbt);
                if (player instanceof ServerPlayerEntity) {
                    if (!MobDatas.containsMobNameOnClient(name)) {
                        MobDatas.saveMobNameOnServer((ServerPlayerEntity) player, name);
                        PlayerUtils.removeSingleItemFromPlayer(player, itemStack.getItem(), 1);
                    } else {
                        player.sendMessage(
                                Texts.ALREADY.withTranslatableTexts(name).getTextComponent(),
                                player.getUniqueID());
                        return new ActionResult<>(ActionResultType.FAIL, itemStack);
                    }
                }
            }
        } catch (Exception e) {
            player.sendMessage(
                    Texts.ERROR.withTranslatableTexts(name).getTextComponent(),
                    player.getUniqueID());
            return new ActionResult<>(ActionResultType.FAIL, itemStack);
        }

        return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
    }

    @Override
    public void addInformation(
            ItemStack stack,
            World world,
            @NotNull List<ITextComponent> tooltip,
            @NotNull ITooltipFlag flag) {
        CompoundNBT nbt = stack.getTag();
        StringBuilder sb;

        if (nbt != null) {
            String name = getEntityNameFromNBT(nbt);

            if (!name.isEmpty()) {
                sb = new StringBuilder().append(Texts.NAME).append(":").append(I18n.format(name));
                tooltip.add(new StringTextComponent(sb.toString()));
            }
        }
    }

    public static String getEntityNameFromNBT(CompoundNBT nbt) {
        return nbt.getString("Name");
    }

    public static void setEntityNameToNBT(String name, CompoundNBT nbt) {
        nbt.putString("Name", name);
    }

    @Override
    public void fillItemGroup(@NotNull ItemGroup tab, @NotNull NonNullList<ItemStack> items) {
        if (this.isInGroup(tab)) {
            for (String name : MobDictionary.getEntityManager().getEntityNames()) {
                if (MobDictionary.getEntityManager().containsName(name)) {
                    ItemStack item = new ItemStack(this);
                    CompoundNBT nbt = new CompoundNBT();
                    setEntityNameToNBT(name, nbt);
                    item.setTag(nbt);
                    items.add(item);
                }
            }
        }
    }
}
