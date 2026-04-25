package org.hhoa.mc.item_information.mobdictionary.messages;

import net.minecraft.util.text.LanguageMap;
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
                                ? LanguageMap.getInstance()
                                        .getLanguageData()
                                        .get(EntityInformation.getModRelevantText(text))
                                : LanguageMap.getInstance().getLanguageData().get(t));
    }
}
