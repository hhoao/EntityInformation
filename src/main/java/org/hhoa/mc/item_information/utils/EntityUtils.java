package org.hhoa.mc.item_information.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

/**
 * EntityUtils
 *
 * @author xianxing
 * @since 2024/11/7
 */
public class EntityUtils {
    public static double getEntityAttribute(LivingEntity entity, Attribute attribute) {
        ModifiableAttributeInstance attributeInstance = entity.getAttribute(attribute);

        if (attributeInstance != null) {
            return attributeInstance.getValue();
        } else {
            return attribute.getDefaultValue();
        }
    }
}
