package org.hhoa.mc.item_information.itemtooltip;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.hhoa.mc.item_information.itemtooltip.item.ItemInfo;

public class ItemTooltipService {
    private static final Gson GSON = new Gson();

    public ItemInfo readItemInfo(InputStream stream) throws IOException {
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, ItemInfo.class);
        }
    }

    public ItemInfo readClientItemInfo(ResourceLocation location) throws IOException {
        var resource = Minecraft.getInstance()
                .getResourceManager()
                .getResource(location)
                .orElseThrow(() -> new IOException("Missing resource " + location));
        try (var stream = resource.open()) {
            return readItemInfo(stream);
        }
    }

    public Optional<ItemInfo> tryReadItemInfo(ResourceLocation location) {
        try {
            return Optional.of(readClientItemInfo(location));
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }
}
