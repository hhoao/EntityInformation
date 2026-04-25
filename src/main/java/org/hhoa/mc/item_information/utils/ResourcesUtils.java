package org.hhoa.mc.item_information.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

/**
 * CommonUtils
 *
 * @author xianxing
 * @since 2024/11/7
 */
public class ResourcesUtils {
    public static String readResourceLocationAsString(ResourceLocation resourceLocation)
            throws IOException {
        if (Minecraft.getInstance().getResourceManager().hasResource(resourceLocation)) {
            Resource resource =
                    Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            InputStream inputStream = resource.getInputStream();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
}
