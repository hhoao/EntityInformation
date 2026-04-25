package org.hhoa.mc.item_information.mobdictionary.messages;

import net.minecraft.locale.Language;
import org.hhoa.mc.item_information.EntityInformation;

/**
 * TranslatableMessageEnum
 *
 * @author xianxing
 * @since 2024/11/2
 */
public class TranslatableChatText extends ChatText {
    public TranslatableChatText(String text) {
        this(text, true);
    }

    public TranslatableChatText(String text, boolean withModId) {
        super(
                text,
                (t) ->
                        withModId
                                ? Language.getInstance()
                                        .getOrDefault(EntityInformation.getModRelevantText(text))
                                : Language.getInstance().getOrDefault(t));
    }
}
