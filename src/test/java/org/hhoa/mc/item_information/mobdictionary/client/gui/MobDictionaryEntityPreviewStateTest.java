package org.hhoa.mc.item_information.mobdictionary.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void convertsPreviewBoxToAbsoluteRenderBounds() {
        MobDictionaryEntityPreviewState.PreviewBounds bounds =
                MobDictionaryEntityPreviewState.previewBounds(19, 12, 78, 82, 49, 70)
                        .expanded(7, 10, 7, 4);

        assertEquals(12, bounds.x1());
        assertEquals(2, bounds.y1());
        assertEquals(85, bounds.x2());
        assertEquals(86, bounds.y2());
        assertEquals(49, bounds.centerX());
        assertEquals(70, bounds.centerY());
    }

    @Test
    void fitsLargeEntitiesWithinPreviewBounds() {
        MobDictionaryEntityPreviewState previewState = new MobDictionaryEntityPreviewState(20.0F, 26.0F);
        MobDictionaryEntityPreviewState.PreviewBounds bounds =
                MobDictionaryEntityPreviewState.previewBounds(12, 2, 85, 86, 49, 70);

        assertEquals(22.0F, previewState.fitScale(22.0F, 1.0F, 2.0F, bounds));
        assertEquals(16.4F, previewState.fitScale(22.0F, 4.0F, 4.0F, bounds), 0.01F);
    }

    @Test
    void readsMissingEntityAttributesAsZero() {
        AttributeMap attributes =
                new AttributeMap(
                        AttributeSupplier.builder().add(Attributes.MAX_HEALTH, 12.0D).build());

        assertEquals(
                12.0D,
                MobDictionaryEntityPreviewState.attributeValueOrZero(
                        attributes, Attributes.MAX_HEALTH));
        assertEquals(
                0.0D,
                MobDictionaryEntityPreviewState.attributeValueOrZero(
                        attributes, Attributes.ATTACK_DAMAGE));
    }

    @Test
    void exposesDictionaryTextureSizeForNeoForgeBlit() {
        assertEquals(256, MobDictionaryEntityPreviewState.DICTIONARY_TEXTURE_SIZE);
    }
}
