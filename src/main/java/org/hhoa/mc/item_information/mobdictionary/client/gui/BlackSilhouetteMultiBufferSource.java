package org.hhoa.mc.item_information.mobdictionary.client.gui;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

final class BlackSilhouetteMultiBufferSource implements MultiBufferSource {
    private final MultiBufferSource delegate;

    BlackSilhouetteMultiBufferSource(MultiBufferSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        return new BlackSilhouetteVertexConsumer(delegate.getBuffer(renderType));
    }
}
