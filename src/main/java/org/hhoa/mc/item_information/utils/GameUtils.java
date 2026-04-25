package org.hhoa.mc.item_information.utils;

import net.minecraftforge.fml.common.thread.SidedThreadGroups;

/**
 * ClientUtils
 *
 * @author xianxing
 * @since 2024/11/2
 */
public class GameUtils {
    public static boolean isOnServer() {
        return Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER;
    }
}
