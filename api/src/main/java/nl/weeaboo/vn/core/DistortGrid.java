package nl.weeaboo.vn.core;

import java.io.Serializable;

/**
 * Represents a two-dimensional {@code (width, height)} grid of X/Y offsets.
 */
public final class DistortGrid implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int width;
    private final int height;
    private final int scansize;

    private boolean sharedBuffer;
    private float[] data;

    public DistortGrid(int w, int h) {
        if (w < 1 || h < 1) {
            throw new IllegalArgumentException("Invalid size (" + w + "x" + h + "), must be at least 2x2");
        }

        width = w;
        height = h;
        scansize = (w + 1) * 2;
        data = new float[getRequiredElements(w, h)];
    }

    private DistortGrid(DistortGrid other) {
        sharedBuffer = true;
        other.sharedBuffer = true;
        data = other.data;
        width = other.width;
        height = other.height;
        scansize = other.scansize;
    }

    /**
     * Creates a copy of the distort grid.
     */
    public DistortGrid copy() {
        return new DistortGrid(this);
    }

    private static int getRequiredElements(int w, int h) {
        return (w + 1) * (h + 1) * 2;
    }

    /**
     * @return The stored X-offset, or {@code 0} if the given {@code (x, y)} coordinate is out of bounds.
     */
    public float getDistortX(int x, int y) {
        if (x < 0 || y < 0 || x > width || y > height) {
            return 0;
        }
        return data[y * scansize + x * 2];
    }

    /**
     * @return The stored X-offset, or {@code 0} if the given {@code (x, y)} coordinate is out of bounds.
     */
    public float getDistortY(int x, int y) {
        if (x < 0 || y < 0 || x > width || y > height) {
            return 0;
        }
        return data[y * scansize + x * 2 + 1];
    }

    /**
     * The width of the grid. Both edges of the grid (0 and width) are valid coordinates.
     */
    public int getWidth() {
        return width;
    }

    /**
     * The height of the grid. Both edges of the grid (0 and height) are valid coordinates.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the XY-offset for the given grid position.
     *
     * @throws ArrayIndexOutOfBoundsException If the given coordinates are outside the bounds of this distort
     *         grid.
     */
    public void setDistort(int x, int y, float dx, float dy) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new ArrayIndexOutOfBoundsException("(" + x + ", " + y + ")");
        }

        if (sharedBuffer) {
            float[] oldData = data;
            data = new float[getRequiredElements(width, height)];
            System.arraycopy(oldData, 0, data, 0, data.length);
            sharedBuffer = false;
        }

        data[y * scansize + x * 2    ] = dx;
        data[y * scansize + x * 2 + 1] = dy;
    }

}
