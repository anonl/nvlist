package nl.weeaboo.vn.core;

import org.junit.Assert;
import org.junit.Test;

public class DistortGridTest {

    @Test
    public void basicUse() {
        DistortGrid grid = new DistortGrid(2, 2);

        // Assert initial values are all zero
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Assert.assertEquals(0f, grid.getDistortX(x, y), 0f);
                Assert.assertEquals(0f, grid.getDistortY(x, y), 0f);
            }
        }

        // Set some arbitrary cells
        grid.setDistort(1, 0, 1f, 2f);
        grid.setDistort(0, 1, 3f, 4f);
        Assert.assertEquals(1f, grid.getDistortX(1, 0), 0f);
        Assert.assertEquals(4f, grid.getDistortY(0, 1), 0f);
    }

    @Test
    public void invalidCells() {
        int w = 2;
        int h = 2;
        DistortGrid grid = new DistortGrid(w, h);
        fill(grid, 1f, 1f);

        final int[] indices = {
            -1, 1,
            1, -1,
            w, 0,
            0, h,
            Integer.MIN_VALUE, 1
        };

        for (int n = 0; n < indices.length; n += 2) {
            int x = indices[n];
            int y = indices[n + 1];
            try {
                grid.setDistort(x, y, 0f, 0f);
                Assert.fail("Writing to an invalid cell somehow worked: " + x + "x" + y);
            } catch (RuntimeException re) {
                // This is expected
            }

            // Reading from invalid cells returns 0f (this in convenient behavior during rendering)
            Assert.assertEquals(0f, grid.getDistortX(x, y), 0f);
            Assert.assertEquals(0f, grid.getDistortY(x, y), 0f);
        }
    }

    @Test
    public void invalidDimensions() {
        final int[] dims = {
            -1, 1,
            1, -1,
            0, 1,
            1, 0,
            Integer.MIN_VALUE, 1
        };

        for (int n = 0; n < dims.length; n += 2) {
            int w = dims[n];
            int h = dims[n + 1];
            try {
                DistortGrid grid = new DistortGrid(w, h);
                Assert.assertEquals(w, grid.getWidth());
                Assert.assertEquals(h, grid.getHeight());
                Assert.fail("Initializing a distort grid with invalid dimensions somehow worked: " + w + "x" + h);
            } catch (RuntimeException re) {
                // This is expected
            }
        }
    }

    @Test
    public void copyOnWrite() {
        int w = 2;
        int h = 2;

        final DistortGrid alpha = new DistortGrid(w, h);
        final DistortGrid beta = alpha.copy();

        // Writing to alpha doesn't affect the copy
        alpha.setDistort(0, 0, 1f, 2f);
        Assert.assertEquals(1f, alpha.getDistortX(0, 0), 0f);
        Assert.assertEquals(2f, alpha.getDistortY(0, 0), 0f);
        Assert.assertEquals(0f, beta.getDistortX(0, 0), 0f);
        Assert.assertEquals(0f, beta.getDistortY(0, 0), 0f);

        // Writing to the copy doesn't affect the original
        DistortGrid copy = alpha.copy();
        copy.setDistort(0, 0, 3f, 4f);
        Assert.assertEquals(1f, alpha.getDistortX(0, 0), 0f);
        Assert.assertEquals(2f, alpha.getDistortY(0, 0), 0f);
        Assert.assertEquals(3f, copy.getDistortX(0, 0), 0f);
        Assert.assertEquals(4f, copy.getDistortY(0, 0), 0f);
    }

    private static void fill(DistortGrid grid, float dx, float dy) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                grid.setDistort(x, y, dx, dy);
            }
        }
    }

}
