package org.hhoa.mc.item_information.mobdictionary.item;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.antlr.v4.runtime.misc.NotNull;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.client.gui.MobDictionaryGui;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;

public class MobDictionaryItem extends Item {

    public MobDictionaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(
            World worldIn, @NotNull PlayerEntity playerIn, @NotNull Hand hand) {
        if (!worldIn.isRemote()) {
            this.displayDictionary();
        }

        return ActionResult.resultSuccess(playerIn.getHeldItem(hand));
    }

    @OnlyIn(Dist.CLIENT)
    private void displayDictionary() {
        MobDictionaryGui guiMobDictionary = new MobDictionaryGui();
        Minecraft.getInstance().displayGuiScreen(guiMobDictionary);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(
            @NotNull ItemStack stack,
            World world,
            List<ITextComponent> tooltip,
            @NotNull ITooltipFlag flag) {
        String sb =
                Texts.REGISTERED_VALUE.getText()
                        + ": "
                        + MobDatas.getRegisteredMobCountOnClient()
                        + '/'
                        + MobDictionary.getEntityManager().getAllMobCount();
        tooltip.add(new StringTextComponent(sb));
    }
}
