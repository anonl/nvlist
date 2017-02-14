package nl.weeaboo.vn.impl.render;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;

public final class TriangleGrid implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum TextureWrap {
        CLAMP,
        REPEAT_X,
        REPEAT_Y,
        REPEAT_BOTH;
    }

    private final int verticesPerRow;
    private final int rows;
    private final int cols;
    private final float[] pos;
    private final float[][] tex;

    private TriangleGrid(int rows, int cols, float[] pos, float[][] tex) {
        this.rows = rows;
        this.cols = cols;
        this.pos = pos;
        this.tex = tex;

        verticesPerRow = cols * 2;
    }

    /**
     * Constructs a combined mesh from one or more layers of textured quads.
     */
    public static TriangleGrid layout(TriangleGridLayer... inputs) {
        Checks.checkRange(inputs.length, "inputs.length", 1);

        double[] xsplits = new double[inputs.length * 2];
        double[] ysplits = new double[inputs.length * 2];

        int t = 0;
        for (TriangleGridLayer input : inputs) {
            Area2D r = input.bounds;
            xsplits[t] = r.x;
            ysplits[t] = r.y;
            t++;
            xsplits[t] = r.x + r.w;
            ysplits[t] = r.y + r.h;
            t++;
        }
        Arrays.sort(xsplits);
        Arrays.sort(ysplits);

        int cols = xsplits.length;
        int rows = ysplits.length - 1;
        int vertices = (2 * cols) * rows;
        FloatBuffer pos = FloatBuffer.allocate(2 * vertices);
        FloatBuffer[] texs = new FloatBuffer[inputs.length];
        for (int n = 0; n < texs.length; n++) {
            texs[n] = FloatBuffer.allocate(2 * vertices);
        }

        for (int yi = 0; yi < rows; yi++) {
            double y0 = ysplits[yi];
            double y1 = ysplits[yi + 1];

            for (int xi = 0; xi < cols; xi++) {
                double x = xsplits[xi];

                glDrawArrayVertex(pos, x, y0);
                for (int n = 0; n < texs.length; n++) {
                    glDrawArrayTexcoord(texs[n], x, y0, inputs[n]);
                }

                glDrawArrayVertex(pos, x, y1);
                for (int n = 0; n < texs.length; n++) {
                    glDrawArrayTexcoord(texs[n], x, y1, inputs[n]);
                }
            }
        }

        float[] posArray = pos.array();
        float[][] texArrays = new float[texs.length][];
        for (int n = 0; n < texArrays.length; n++) {
            texArrays[n] = texs[n].array();
        }
        return new TriangleGrid(rows, cols, posArray, texArrays);
    }

    private static void glDrawArrayVertex(FloatBuffer coords, double x, double y) {
        coords.put((float)x);
        coords.put((float)y);
    }

    private static void glDrawArrayTexcoord(FloatBuffer coords, double x, double y, TriangleGridLayer input) {
        final Area2D bounds = input.bounds;
        final Area2D texBounds = input.texBounds;
        final TextureWrap wrap = input.wrap;

        double normalizedX = (x - bounds.x) / bounds.w;
        double normalizedY = (y - bounds.y) / bounds.h;

        double u;
        if (wrap != TextureWrap.REPEAT_X && wrap != TextureWrap.REPEAT_BOTH) {
            u = texBounds.x + Math.max(0, Math.min(1, normalizedX)) * texBounds.w;
        } else {
            u = texBounds.x + normalizedX * texBounds.w;
        }

        double v;
        if (wrap != TextureWrap.REPEAT_Y && wrap != TextureWrap.REPEAT_BOTH) {
            v = texBounds.y + Math.max(0, Math.min(1, normalizedY)) * texBounds.h;
        } else {
            v = texBounds.y + normalizedY * texBounds.h;
        }

        coords.put((float)u);
        coords.put(1 - (float)v);
    }

    /**
     * @return The number of vertices in each row of the mesh.
     */
    public int getVerticesPerRow() {
        return verticesPerRow;
    }

    /**
     * Copies the mesh vertex coordinates to the supplied output buffer.
     */
    public void getVertices(int row, FloatBuffer out, int outStride) {
        int src = row * verticesPerRow * 2;
        int dst = out.position();
        for (int n = 0; n < verticesPerRow; n++) {
            out.put(dst++, pos[src++]);
            out.put(dst++, pos[src++]);
            dst += outStride;
        }
    }

    /**
     * Copies the mesh texture coordinates to the supplied output buffer.
     */
    public void getTexCoords(int texIndex, int row, FloatBuffer out, int outStride) {
        int src = row * verticesPerRow * 2;
        int dst = out.position();
        for (int n = 0; n < verticesPerRow; n++) {
            out.put(dst++, tex[texIndex][src++]);
            out.put(dst++, tex[texIndex][src++]);
            dst += outStride;
        }
    }

    /**
     * @return The number of rows in the mesh.
     */
    public int getRows() {
        return rows;
    }

    /**
     * @return The number of columns in the mesh.
     */
    public int getCols() {
        return cols;
    }

    /**
     * @return The number of different textures used in the mesh.
     */
    public int getTextures() {
        return tex.length;
    }

    /**
     * @return The memory layout used by the mesh.
     */
    public VertexAttributes getVertexAttributes() {
        List<VertexAttribute> list = Lists.newArrayList();
        list.add(new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));
        list.add(new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
        for (int t = 0; t < getTextures(); t++) {
            list.add(new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + t));
        }
        return new VertexAttributes(list.toArray(new VertexAttribute[list.size()]));
    }

    public static class TriangleGridLayer {

        public final Area2D bounds;
        public final Area2D texBounds;
        public final TextureWrap wrap;

        public TriangleGridLayer(Area2D bounds, Area2D texBounds, TextureWrap wrap) {
            this.bounds = bounds;
            this.texBounds = texBounds;
            this.wrap = wrap;
        }

    }

}
