package org.hhoa.mc.item_information.itemtooltip.item;

import javax.annotation.Nonnull;
import org.antlr.v4.runtime.misc.NotNull;

public enum TAG {
    GET("获取"),
    USE("用途"),
    GENERATE("生成"),
    DESC("简介");

    @Nonnull private final String cn;

    TAG(@Nonnull String cn) {
        this.cn = cn;
    }

    public @NotNull String getCn() {
        return cn;
    }
}
