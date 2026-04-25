package org.hhoa.mc.item_information.mobdictionary.client.gui;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;

record MobDictionaryEntityPreviewState(float minScale, float maxScale) {
    private static final int MAX_PREVIEW_BRIGHTNESS =
            LightTexture.block(LightTexture.pack(15, 15))
                    + LightTexture.sky(LightTexture.pack(15, 15));
    private static final int MAX_SHADE_ALPHA = 0xA0;

    float clampScale(float candidate) {
        return Mth.clamp(candidate, minScale, maxScale);
    }

    static int packedLight(boolean unlocked) {
        return unlocked ? LightTexture.pack(15, 15) : LightTexture.pack(0, 0);
    }

    PreviewEntityRotation previewEntityRotation(float horizontalRotation, float verticalRotation) {
        float yaw = 180.0F + horizontalRotation;
        return new PreviewEntityRotation(yaw, verticalRotation, yaw, yaw, yaw);
    }

    int previewShadeColor(boolean unlocked) {
        int packedLight = packedLight(unlocked);
        int brightness = LightTexture.block(packedLight) + LightTexture.sky(packedLight);
        int alpha = Math.round((1.0F - (float) brightness / MAX_PREVIEW_BRIGHTNESS) * MAX_SHADE_ALPHA);
        return alpha << 24;
    }

    record PreviewEntityRotation(float yRot, float xRot, float bodyRot, float headRot, float headRotO) {}
}
