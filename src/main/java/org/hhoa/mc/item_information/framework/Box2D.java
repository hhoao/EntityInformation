package org.hhoa.mc.item_information.framework;

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

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }
}
