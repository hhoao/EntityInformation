package org.hhoa.mc.item_information.framework;

import net.minecraft.SharedConstants;

/**
 * Box
 *
 * @author xianxing
 * @since 2024/11/8
 */
public class Box2D {
    private double minX;
    private double minY;
    private double maxY;
    private double maxX;

    public Box2D(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        if (minX > maxX || minY > maxY) {
            String s = "Invalid bounding box data, inverted bounds for: " + this;
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw new IllegalStateException(s);
            }
            this.minX = Math.min(minX, maxX);
            this.minY = Math.min(minY, maxY);
            this.maxX = Math.max(minX, maxX);
            this.maxY = Math.max(minY, maxY);
        }
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public boolean isInBox(double x, double y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
