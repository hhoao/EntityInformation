package org.hhoa.mc.item_information.mobdictionary.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.joml.Quaternionf;
import org.joml.Vector3f;

final class LockedEntityRenderer extends PictureInPictureRenderer<LockedEntityRenderState> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    LockedEntityRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        this.entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    }

    @Override
    public Class<LockedEntityRenderState> getRenderStateClass() {
        return LockedEntityRenderState.class;
    }

    @Override
    protected void renderToTexture(LockedEntityRenderState renderState, PoseStack poseStack) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        Vector3f translation = renderState.translation();
        poseStack.translate(translation.x, translation.y, translation.z);
        poseStack.mulPose(renderState.rotation());
        Quaternionf cameraRotation = renderState.overrideCameraAngle();
        if (cameraRotation != null) {
            entityRenderDispatcher.overrideCameraOrientation(
                    cameraRotation.conjugate(new Quaternionf()).rotateY((float) Math.PI));
        }

        entityRenderDispatcher.setRenderShadow(false);
        try {
            entityRenderDispatcher.render(
                    renderState.renderState(),
                    0.0,
                    0.0,
                    0.0,
                    poseStack,
                    new BlackSilhouetteMultiBufferSource(bufferSource),
                    15728880);
        } finally {
            entityRenderDispatcher.setRenderShadow(true);
        }
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "locked_entity";
    }
}
