package org.hhoa.mc.item_information.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

/**
 * EntityUtils
 *
 * @author xianxing
 * @since 2024/11/7
 */
public class EntityUtils {
    public static double getEntityAttribute(LivingEntity entity, Attribute attribute) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);

        if (attributeInstance != null) {
            return attributeInstance.getValue();
        } else {
            return attribute.getDefaultValue();
        }
    }
}
