package org.hhoa.mc.item_information.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hhoa.mc.item_information.ModInfo;

/**
 * LoggerUtils
 *
 * @author xianxing
 * @since 2024/10/19
 */
public class LoggerUtils {
    private static final String IDENTIFIER = ModInfo.ID;
    public static final Logger LOGGER = LoggerUtils.getLogger(LoggerUtils.class);

    public static Logger getLogger(Class<?> c) {
        org.apache.logging.log4j.Logger logger;
        logger = LogManager.getLogger(String.format("[%s][%s]", IDENTIFIER, c.getCanonicalName()));
        return logger;
    }
}
