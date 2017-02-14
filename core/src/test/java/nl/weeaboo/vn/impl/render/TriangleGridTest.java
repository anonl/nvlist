package nl.weeaboo.vn.impl.render;

import static nl.weeaboo.vn.impl.render.TriangleGridTestUtil.assertCols;
import static nl.weeaboo.vn.impl.render.TriangleGridTestUtil.assertRows;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.impl.render.TriangleGrid.TextureWrap;
import nl.weeaboo.vn.impl.render.TriangleGrid.TriangleGridLayer;
import nl.weeaboo.vn.impl.render.TriangleGridTestUtil.DataType;

public class TriangleGridTest {

    @Test
    public void simpleLayout1() {
        TriangleGrid grid = TriangleGrid.layout(
                new TriangleGridLayer(Area2D.of(1, 2, 3, 4), Area2D.of(0, 0, 1, 1), TextureWrap.CLAMP)
        );

        // Check general properties
        Assert.assertEquals(1, grid.getTextures());
        Assert.assertEquals(3, grid.getVertexAttributes().size()); // pos, color, tex1

        // Check vertex data
        assertCols(grid, DataType.VERTEX, 1, 4);
        assertRows(grid, DataType.VERTEX, 2, 6);
    }

    @Test
    public void simpleLayout2() {
        TriangleGrid grid = TriangleGrid.layout(
                new TriangleGridLayer(Area2D.of(0, 0, 4, 2), Area2D.of(0, 0, 1, 1), TextureWrap.REPEAT_X),
                new TriangleGridLayer(Area2D.of(2, 1, 4, 2), Area2D.of(0, 0, 1, 1), TextureWrap.REPEAT_Y)
        );

        // Check general properties
        Assert.assertEquals(2, grid.getTextures());
        Assert.assertEquals(4, grid.getVertexAttributes().size()); // pos, color, tex1, tex2

        /*
         * 1111..
         * 11xx22
         * ..2222
         */
        // Check vertex data
        assertCols(grid, DataType.VERTEX, 0, 2, 4, 6);
        assertRows(grid, DataType.VERTEX, 0, 1, 2, 3);
        // UV coordinates are y-flipped compared to the vertex coordinates
        assertCols(grid, DataType.TEX1, 0, .5, 1, 1.5);
        assertRows(grid, DataType.TEX1, 1, .5, 0, 0);
        assertCols(grid, DataType.TEX2, 0, 0, .5, 1);
        assertRows(grid, DataType.TEX2, 1.5, 1, .5, 0);
    }

}
