package org.hhoa.mc.item_information.mobdictionary.messages;

import java.util.Arrays;
import java.util.IllegalFormatException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;

public class ChatText {
    protected String text;
    private final Convertor convertor;

    public ChatText(String text) {
        this(text, null);
    }

    public ChatText(String text, Convertor convertor) {
        this.text = text;
        this.convertor = convertor;
    }

    public String getText() {
        if (convertor != null) {
            return convertor.apply(this.text);
        } else {
            return text;
        }
    }

    public ChatText withText(Object... p_118940_) {
        try {
            return new ChatText(String.format(getText(), p_118940_));
        } catch (IllegalFormatException illegalformatexception) {
            return new ChatText("Format error: " + illegalformatexception);
        }
    }

    public ChatText withTranslatableTexts(String... texts) {
        try {
            Object[] array =
                    Arrays.stream(texts)
                            .map((str) -> LanguageMap.getInstance().getLanguageData().get(str))
                            .toArray();
            return new ChatText(String.format(getText(), array));
        } catch (IllegalFormatException illegalformatexception) {
            return new ChatText("Format error: " + illegalformatexception);
        }
    }

    public ITextComponent getTextComponent() {
        return new StringTextComponent(getText());
    }

    @Override
    public String toString() {
        return getText();
    }

    public interface Convertor {
        String apply(String text);
    }
}
