package org.hhoa.mc.item_information.mobdictionary.client.gui;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

record MobDictionaryEntityPreviewState(float minScale, float maxScale) {
    static final int DICTIONARY_TEXTURE_SIZE = 256;
    private static final float FIT_PADDING = 0.8986F;

    float clampScale(float candidate) {
        return Mth.clamp(candidate, minScale, maxScale);
    }

    static double attributeValueOrZero(AttributeMap attributes, Holder<Attribute> attribute) {
        return attributes.hasAttribute(attribute) ? attributes.getValue(attribute) : 0.0D;
    }

    float fitScale(float candidate, float entityWidth, float entityHeight, PreviewBounds bounds) {
        float safeEntityWidth = Math.max(entityWidth, 0.1F);
        float safeEntityHeight = Math.max(entityHeight, 0.1F);
        float fitWidth = bounds.width() * FIT_PADDING / safeEntityWidth;
        float fitHeight = bounds.height() * FIT_PADDING / safeEntityHeight;
        return Math.min(candidate, Math.min(fitWidth, fitHeight));
    }

    PreviewEntityRotation previewEntityRotation(float horizontalRotation, float verticalRotation) {
        float yaw = 180.0F + horizontalRotation;
        return new PreviewEntityRotation(yaw, verticalRotation, yaw, yaw, yaw);
    }

    static PreviewBounds previewBounds(int x1, int y1, int x2, int y2, int centerX, int centerY) {
        return new PreviewBounds(x1, y1, x2, y2, centerX, centerY);
    }

    record PreviewEntityRotation(float yRot, float xRot, float bodyRot, float headRot, float headRotO) {}

    record PreviewBounds(int x1, int y1, int x2, int y2, int centerX, int centerY) {
        PreviewBounds expanded(int left, int top, int right, int bottom) {
            return new PreviewBounds(x1 - left, y1 - top, x2 + right, y2 + bottom, centerX, centerY);
        }

        int width() {
            return x2 - x1;
        }

        int height() {
            return y2 - y1;
        }
    }
}
