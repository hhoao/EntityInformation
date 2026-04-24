package org.hhoa.mc.item_information;

import net.minecraft.resources.ResourceLocation;

public final class ModInfo {
    public static final String ID = "entity_information";
    public static final String NAME = "EntityInformation";

    private ModInfo() {}

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
