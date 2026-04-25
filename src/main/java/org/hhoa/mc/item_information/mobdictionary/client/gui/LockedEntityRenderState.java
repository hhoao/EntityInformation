package org.hhoa.mc.item_information.mobdictionary.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

record LockedEntityRenderState(
        EntityRenderState renderState,
        Vector3f translation,
        Quaternionf rotation,
        @Nullable Quaternionf overrideCameraAngle,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds)
        implements PictureInPictureRenderState {
    LockedEntityRenderState(
            EntityRenderState renderState,
            Vector3f translation,
            Quaternionf rotation,
            @Nullable Quaternionf overrideCameraAngle,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            @Nullable ScreenRectangle scissorArea) {
        this(
                renderState,
                translation,
                rotation,
                overrideCameraAngle,
                x0,
                y0,
                x1,
                y1,
                scale,
                scissorArea,
                PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }
}
