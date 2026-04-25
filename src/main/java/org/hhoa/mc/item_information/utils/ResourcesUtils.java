package org.hhoa.mc.item_information.utils;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;

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
            IResource resource =
                    Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
            InputStream inputStream = resource.getInputStream();
            return new String(ByteStreams.toByteArray(inputStream), StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
}
