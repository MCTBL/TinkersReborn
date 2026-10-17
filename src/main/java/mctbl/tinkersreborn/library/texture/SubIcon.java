
package mctbl.tinkersreborn.library.texture;

import net.minecraft.util.IIcon;

public class SubIcon implements IIcon {

    private final IIcon parent;
    private final float minU, maxU, minV, maxV;

    public SubIcon(IIcon parent, int col, int row, int cols, int rows) {
        this.parent = parent;
        float uSpan = parent.getMaxU() - parent.getMinU();
        float vSpan = parent.getMaxV() - parent.getMinV();
        this.minU = parent.getMinU() + uSpan * col / cols;
        this.maxU = parent.getMinU() + uSpan * (col + 1) / cols;
        this.minV = parent.getMinV() + vSpan * row / rows;
        this.maxV = parent.getMinV() + vSpan * (row + 1) / rows;
    }

    public SubIcon(IIcon parent, int col, int row) {
        this(parent, col, row, 16, 16);
    }

    @Override
    public float getMinU() {
        return minU;
    }

    @Override
    public float getMaxU() {
        return maxU;
    }

    @Override
    public float getMinV() {
        return minV;
    }

    @Override
    public float getMaxV() {
        return maxV;
    }

    @Override
    public float getInterpolatedU(double u) {
        float width = maxU - minU;
        return minU + width * (float) u / 16.0F;
    }

    @Override
    public float getInterpolatedV(double v) {
        float height = maxV - minV;
        return minV + height * (float) v / 16.0F;
    }

    @Override
    public String getIconName() {
        return parent.getIconName();
    }

    @Override
    public int getIconWidth() {
        return parent.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return parent.getIconHeight();
    }

}
