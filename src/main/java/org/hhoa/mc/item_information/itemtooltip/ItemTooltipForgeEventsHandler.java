package org.hhoa.mc.item_information.itemtooltip;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.hhoa.mc.item_information.ModInfo;
import org.hhoa.mc.item_information.config.Configs;
import org.hhoa.mc.item_information.itemtooltip.item.ItemInfo;
import org.hhoa.mc.item_information.itemtooltip.kaymap.ItemTooltipKeyMappingRegistry;
import org.hhoa.mc.item_information.utils.LoggerUtils;

public class ItemTooltipForgeEventsHandler {
    private static final Logger LOG = LoggerUtils.getLogger(ItemTooltipForgeEventsHandler.class);
    private static final Cache<ResourceLocation, Set<Component>> itemCache =
            CacheBuilder.newBuilder().maximumSize(64).build();
    private static final ItemTooltipService ITEM_TOOLTIP_SERVICE = new ItemTooltipService();

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) throws IOException {
        if (Configs.enableItemToolTip) {
            ResourceLocation registryName =
                    BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
            List<Component> toolTip = event.getToolTip();
            if (registryName != null) {
                String name = toolTip.get(0).getString();
                Set<Component> itemInfoComponents = null;
                String resourcePath =
                        String.format(
                                "item_infos/%s/%s.json",
                                registryName.getNamespace(), registryName.getPath());
                try {
                    itemInfoComponents =
                            itemCache.get(
                                    registryName,
                                    () -> {
                                        return ITEM_TOOLTIP_SERVICE
                                                .tryReadItemInfo(ModInfo.location(resourcePath))
                                                .map(itemInfo -> buildTooltipComponents(itemInfo, name))
                                                .orElseGet(Set::of);
                                    });
                } catch (Exception ignored) {
                }
                if (itemInfoComponents != null && !itemInfoComponents.isEmpty()) {
                    Component nameComponent = toolTip.remove(0);
                    ArrayList<Component> components = new ArrayList<>(toolTip);
                    toolTip.clear();
                    toolTip.add(nameComponent);
                    toolTip.addAll(components);
                    toolTip.addAll(itemInfoComponents);
                }
            }
        }
    }

    private static Set<Component> buildTooltipComponents(ItemInfo itemInfo, String name) {
        Map<String, Set<String>> infos = itemInfo.getInfos();
        Set<Component> descComponent = new LinkedHashSet<>();
        descComponent.add(getDescComponent(infos.get("简介"), name));
        descComponent.add(getUseComponent(infos.get("用途")));
        descComponent.add(getGetComponent(infos.get("获取")));
        descComponent.add(getGenerateComponent(infos.get("生成")));
        descComponent.remove(null);
        return descComponent;
    }

    private static Component getGenerateComponent(Set<String> generates) {
        if (generates != null) {
            String[] array = generates.toArray(String[]::new);
            String str = String.join(",", array);
            return Component.literal("生成: " + str);
        }
        return null;
    }

    private static Component getGetComponent(Set<String> get) {
        if (get != null) {
            String[] array = get.toArray(String[]::new);
            String str = String.join(",", array);
            return Component.literal("获取途径: " + str);
        }
        return null;
    }

    private static Component getUseComponent(Set<String> usages) {
        if (usages != null) {
            String[] array = usages.toArray(String[]::new);
            String descStr = String.join(",", array);
            return Component.literal("用途: " + descStr);
        }
        return null;
    }

    private static Component getDescComponent(Set<String> descriptions, String realName) {
        if (descriptions != null) {
            String singleDesc = null;
            Iterator<String> iterator = descriptions.iterator();
            while (iterator.hasNext()) {
                String description = iterator.next();
                if (description == null || description.isBlank()) {
                    iterator.remove();
                } else {
                    int is = description.indexOf("是");
                    if (is != -1) {
                        String name = description.substring(0, is);
                        if (name.equals(realName)) {
                            singleDesc = description;
                            break;
                        }
                    }
                }
            }

            if (singleDesc == null) {
                String[] array = descriptions.toArray(String[]::new);
                singleDesc = String.join("\n", array);
            }
            return Component.literal(singleDesc);
        }
        return null;
    }

    @SubscribeEvent
    public static void onKeyboardKeyPressedEventPost(ScreenEvent.KeyPressed.Post event) {
        Screen screen = event.getScreen();
        try {
            if (screen instanceof AbstractContainerScreen) {
                if (event.getKeyCode()
                        == ItemTooltipKeyMappingRegistry.SEARCH.getKey().getValue()) {
                    Slot slotUnderMouse = ((AbstractContainerScreen<?>) screen).getSlotUnderMouse();
                    if (slotUnderMouse != null) {
                        openItemSearchWeb(slotUnderMouse.getItem());
                    }
                } else if (event.getKeyCode()
                        == ItemTooltipKeyMappingRegistry.TOGGLE_TOOLTIP.getKey().getValue()) {
                    Configs.enableItemToolTip = !Configs.enableItemToolTip;
                } else if (event.getKeyCode()
                        == ItemTooltipKeyMappingRegistry.CHANGE_SEARCH_ENGINE
                                .getKey()
                                .getValue()) {
                    Configs.useWiki = !Configs.useWiki;
                }
            }
        } catch (Exception e) {
            LoggerUtils.LOGGER.error(e);
        }
    }

    public static void openItemSearchWebOnWiki(ItemStack stack) throws IOException {
        LanguageManager languageManager = Minecraft.getInstance().getLanguageManager();
        String name = languageManager.getSelected().split("_")[0];
        String itemName = I18n.get(stack.getItem().getDescriptionId()).replace(" ", "_");
        String apiUrl = String.format("https://%s.minecraft.wiki/w/%s", name, itemName);
        openBrowser(apiUrl);
    }

    public static void openItemSearchWeb(ItemStack stack) throws IOException {
        LOG.info("Open {}", stack);
        if (Configs.useWiki) {
            openItemSearchWebOnWiki(stack);
        } else {
            openItemSearchWebOnMcmod(stack);
        }
    }

    public static void openItemSearchWebOnMcmod(ItemStack stack) throws IOException {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String creatorModId = registryName.getNamespace();
        if (Minecraft.getInstance().level != null) {
            String itemCreatorModId =
                    stack.getItem()
                            .getCreatorModId(Minecraft.getInstance().level.registryAccess(), stack);
            if (itemCreatorModId != null) {
                creatorModId = itemCreatorModId;
            }
        }
        String modName = URLEncoder.encode(creatorModId, StandardCharsets.UTF_8);
        String regName = URLEncoder.encode(registryName.toString(), StandardCharsets.UTF_8);
        String displayName =
                URLEncoder.encode(stack.getDisplayName().getString(), StandardCharsets.UTF_8);
        URL apiUrl = new URL(String.format("https://api.mcmod.cn/getItem/?regname=%s", regName));
        int mcModApiNum =
                Integer.parseInt(
                        IOUtils.readLines(apiUrl.openStream(), StandardCharsets.UTF_8).get(0));

        String url =
                mcModApiNum > 0
                        ? String.format("https://www.mcmod.cn/item/%d.html", mcModApiNum)
                        : String.format(
                                "https://search.mcmod.cn/s?key=%s+%s", modName, displayName);

        openBrowser(url);
    }

    private static void openBrowser(String url) throws IOException {
        if (!Desktop.isDesktopSupported() && !System.getProperty("os.name").contains("Windows")) {
            Runtime runtime = Runtime.getRuntime();
            if (System.getProperty("os.name").contains("Mac")) {
                runtime.exec(new String[] {"open", url});
            } else {
                runtime.exec(new String[] {"xdg-open", url});
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
        }
    }
}
