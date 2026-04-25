package org.hhoa.mc.item_information.mobdictionary.item;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.hhoa.mc.item_information.mobdictionary.client.gui.MobDictionaryClientScreens;
import org.hhoa.mc.item_information.mobdictionary.data.MobDatas;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;
import org.jetbrains.annotations.NotNull;

public class MobDictionaryItem extends Item {

    public MobDictionaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(
            Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand hand) {
        if (worldIn.isClientSide()) {
            this.displayDictionary();
        }

        return InteractionResult.SUCCESS;
    }

    private void displayDictionary() {
        MobDictionaryClientScreens.openMobDictionary();
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            Item.TooltipContext tooltipContext,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> tooltip,
            @NotNull TooltipFlag flag) {
        String sb =
                Texts.REGISTERED_VALUE.getText()
                        + ": "
                        + MobDatas.getRegisteredMobCountOnClient()
                        + '/'
                        + MobDictionary.getEntityManager().getAllMobCount();
        tooltip.accept(Component.literal(sb));
    }
}
