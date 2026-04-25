package org.hhoa.mc.item_information.utils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * ClientUtils
 *
 * @author xianxing
 * @since 2024/11/2
 */
public class GameUtils {
    public static boolean isOnServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }
}
