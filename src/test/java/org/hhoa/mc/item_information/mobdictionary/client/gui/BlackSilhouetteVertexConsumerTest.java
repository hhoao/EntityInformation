package org.hhoa.mc.item_information.mobdictionary.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.junit.jupiter.api.Test;

class BlackSilhouetteVertexConsumerTest {
    @Test
    void forcesVertexRgbToBlackWhilePreservingAlpha() {
        CapturingVertexConsumer delegate = new CapturingVertexConsumer();
        BlackSilhouetteVertexConsumer silhouette = new BlackSilhouetteVertexConsumer(delegate);

        silhouette.setColor(64, 128, 255, 96);

        assertEquals(0, delegate.red);
        assertEquals(0, delegate.green);
        assertEquals(0, delegate.blue);
        assertEquals(96, delegate.alpha);
    }

    private static final class CapturingVertexConsumer implements VertexConsumer {
        int red;
        int green;
        int blue;
        int alpha;

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
            return this;
        }
    }
}
