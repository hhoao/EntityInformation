package org.hhoa.mc.item_information.mobdictionary.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.junit.jupiter.api.Test;

class MobDictionaryEntityPreviewStateTest {
    @Test
    void clampsScaleWithinConfiguredBounds() {
        MobDictionaryEntityPreviewState previewState = new MobDictionaryEntityPreviewState(20.0F, 26.0F);

        assertEquals(20.0F, previewState.clampScale(19.0F));
        assertEquals(24.0F, previewState.clampScale(24.0F));
        assertEquals(26.0F, previewState.clampScale(27.0F));
    }

    @Test
    void choosesPackedLightFromUnlockState() {
        assertEquals(LightTexture.pack(15, 15), MobDictionaryEntityPreviewState.packedLight(true));
        assertEquals(LightTexture.pack(0, 0), MobDictionaryEntityPreviewState.packedLight(false));
    }

    @Test
    void derivesPreviewEntityRotationFromDragOffsets() {
        MobDictionaryEntityPreviewState previewState = new MobDictionaryEntityPreviewState(20.0F, 26.0F);

        MobDictionaryEntityPreviewState.PreviewEntityRotation rotation =
                previewState.previewEntityRotation(32.0F, -14.0F);

        assertEquals(212.0F, rotation.yRot());
        assertEquals(-14.0F, rotation.xRot());
        assertEquals(212.0F, rotation.bodyRot());
        assertEquals(212.0F, rotation.headRot());
        assertEquals(212.0F, rotation.headRotO());
    }

    @Test
    void derivesPreviewShadeFromPackedLightPolicy() {
        MobDictionaryEntityPreviewState previewState = new MobDictionaryEntityPreviewState(20.0F, 26.0F);

        assertEquals(0x00000000, previewState.previewShadeColor(true));
        assertEquals(0xA0000000, previewState.previewShadeColor(false));
    }

    @Test
    void missingAttributeValueDefaultsToZero() {
        AttributeSupplier supplier =
                AttributeSupplier.builder().add(Attributes.MAX_HEALTH, 10.0D).build();
        AttributeMap attributes = new AttributeMap(supplier);

        assertEquals(
                0.0D,
                MobDictionaryEntityPreviewState.attributeValueOrZero(
                        attributes, Attributes.ATTACK_DAMAGE));
    }
}
