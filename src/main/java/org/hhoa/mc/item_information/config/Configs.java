package org.hhoa.mc.item_information.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Configs {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_ITEM_TOOLTIP =
            BUILDER.comment("Enable item tooltip extension").define("enableItemToolTip", true);
    public static final ModConfigSpec.BooleanValue USE_WIKI =
            BUILDER.comment("Use online wiki lookup").define("useWiki", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static volatile boolean enableItemToolTip = ENABLE_ITEM_TOOLTIP.getDefault();
    public static volatile boolean useWiki = USE_WIKI.getDefault();

    private Configs() {}

    public static void syncFromConfig() {
        applyCompatibilityValues(readValue(ENABLE_ITEM_TOOLTIP), readValue(USE_WIKI));
    }

    static void applyCompatibilityValues(boolean enableItemToolTipValue, boolean useWikiValue) {
        enableItemToolTip = enableItemToolTipValue;
        useWiki = useWikiValue;
    }

    private static boolean readValue(ModConfigSpec.BooleanValue value) {
        return SPEC.isLoaded() ? value.get() : value.getDefault();
    }
}
