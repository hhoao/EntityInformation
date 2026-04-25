package org.hhoa.mc.item_information.mobdictionary.item;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.client.gui.MobDictionaryGui;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.jetbrains.annotations.NotNull;

public class MobDictionaryItem extends Item {

    public MobDictionaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand hand) {
        if (worldIn.isClientSide()) {
            this.displayDictionary();
        }

        return InteractionResultHolder.success(playerIn.getItemInHand(hand));
    }

    @OnlyIn(Dist.CLIENT)
    private void displayDictionary() {
        MobDictionaryGui guiMobDictionary = new MobDictionaryGui();
        Minecraft.getInstance().setScreen(guiMobDictionary);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            Level world,
            List<Component> tooltip,
            @NotNull TooltipFlag flag) {
        String sb =
                Texts.REGISTERED_VALUE.getText()
                        + ": "
                        + MobDatas.getRegisteredMobCountOnClient()
                        + '/'
                        + MobDictionary.getEntityManager().getAllMobCount();
        tooltip.add(new TextComponent(sb));
    }
}
