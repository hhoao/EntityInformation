package org.hhoa.mc.item_information.mobdictionary;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.hhoa.mc.item_information.EntityInformation;
import org.hhoa.mc.item_information.mobdictionary.data.ConfigData;

public class Conditions {
    public static final ResourceLocation ENABLE_NORMAL_CRAFTING =
            EntityInformation.location("enable_normal_crafting");
    public static final ResourceLocation ENABLE_SEWING_CRAFTING =
            EntityInformation.location("enable_sewing_crafting");

    public static void register() {
        CraftingHelper.register(EnableNormalCrafting.Serializer.INSTANCE);
        CraftingHelper.register(EnableSewingCrafting.Serializer.INSTANCE);
    }

    public static class EnableNormalCrafting implements ICondition {
        @Override
        public ResourceLocation getID() {
            return ENABLE_NORMAL_CRAFTING;
        }

        @Override
        public boolean test() {
            return ConfigData.enableNormalCrafting;
        }

        public static class Serializer implements IConditionSerializer<EnableNormalCrafting> {
            public static Serializer INSTANCE = new Serializer();

            @Override
            public void write(JsonObject json, EnableNormalCrafting value) {}

            @Override
            public EnableNormalCrafting read(JsonObject json) {
                return new EnableNormalCrafting();
            }

            @Override
            public ResourceLocation getID() {
                return ENABLE_NORMAL_CRAFTING;
            }
        }
    }

    public static class EnableSewingCrafting implements ICondition {
        @Override
        public ResourceLocation getID() {
            return ENABLE_SEWING_CRAFTING;
        }

        @Override
        public boolean test() {
            return ConfigData.enableSewingKitSupport;
        }

        public static class Serializer implements IConditionSerializer<EnableSewingCrafting> {
            public static Serializer INSTANCE = new Serializer();

            @Override
            public void write(JsonObject json, EnableSewingCrafting value) {}

            @Override
            public EnableSewingCrafting read(JsonObject json) {
                return new EnableSewingCrafting();
            }

            @Override
            public ResourceLocation getID() {
                return ENABLE_SEWING_CRAFTING;
            }
        }
    }
}
