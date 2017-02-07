package nl.weeaboo.vn.impl.render;

import java.nio.FloatBuffer;

import org.junit.Assert;

import nl.weeaboo.vn.impl.test.CoreTestUtil;

public final class TriangleGridTestUtil {

    public enum DataType {
        VERTEX, TEX1, TEX2, TEX3;
    }

    private static final double EPSILON = CoreTestUtil.EPSILON;

    private TriangleGridTestUtil() {
    }

    /** Checks the values at the grid's columns. */
    public static void assertCols(TriangleGrid grid, DataType type, double... colSplits) {
        Assert.assertEquals(colSplits.length, grid.getCols());

        FloatBuffer buf = FloatBuffer.allocate(2 * grid.getVerticesPerRow());
        getVertexData(grid, type, 0, buf);
        for (int n = 0; n < colSplits.length; n++) {
            Assert.assertEquals("n=" + n, colSplits[n], buf.get(4 * n), EPSILON);
        }
    }

    /** Checks the values at the grid's rows. */
    public static void assertRows(TriangleGrid grid, DataType type, double... rowSplits) {
        Assert.assertEquals(rowSplits.length - 1, grid.getRows());

        FloatBuffer buf = FloatBuffer.allocate(2 * grid.getVerticesPerRow());
        for (int y = 0; y < grid.getRows(); y++) {
            getVertexData(grid, type, y, buf);

            // Vertices are in the order: x0,y0,x0,y1,...
            Assert.assertEquals("y0=" + y, rowSplits[y + 0], buf.get(1), EPSILON);
            Assert.assertEquals("y1=" + y, rowSplits[y + 1], buf.get(3), EPSILON);
        }
    }

    private static void getVertexData(TriangleGrid grid, DataType type, int row, FloatBuffer out) {
        switch (type) {
        case VERTEX:
            grid.getVertices(row, out, 0);
            break;
        case TEX1:
            grid.getTexCoords(0, row, out, 0);
            break;
        case TEX2:
            grid.getTexCoords(1, row, out, 0);
            break;
        case TEX3:
            grid.getTexCoords(2, row, out, 0);
            break;
        default:
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

}
