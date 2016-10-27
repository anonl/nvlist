package nl.weeaboo.vn.render.impl;

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

public final class TriangleGrid implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TextureWrap {
		CLAMP,
		REPEAT_X,
		REPEAT_Y,
		REPEAT_BOTH;
	}

	private final int verticesPerRow;
	private final int rows, cols;
	private final float[] pos;
	private final float[][] tex;

	private TriangleGrid(int rows, int cols, float[] pos, float[][] tex) {
		this.rows = rows;
		this.cols = cols;
        this.pos = pos;
        this.tex = tex;

		verticesPerRow = cols * 2;
	}

	public static TriangleGrid layout1(Area2D bounds0, Area2D texBounds0, TextureWrap wrap0) {
        return layout(new InputQuad(bounds0, texBounds0, wrap0));
	}
	public static TriangleGrid layout2(Area2D bounds0, Area2D texBounds0, TextureWrap wrap0,
			Area2D bounds1, Area2D texBounds1, TextureWrap wrap1)
	{
        return layout(new InputQuad(bounds0, texBounds0, wrap0), new InputQuad(bounds1, texBounds1, wrap1));
	}
	public static TriangleGrid layout3(Area2D bounds0, Area2D texBounds0, TextureWrap wrap0,
			Area2D bounds1, Area2D texBounds1, TextureWrap wrap1,
			Area2D bounds2, Area2D texBounds2, TextureWrap wrap2)
	{
		return layout(
		        new InputQuad(bounds0, texBounds0, wrap0),
		        new InputQuad(bounds1, texBounds1, wrap1),
		        new InputQuad(bounds2, texBounds2, wrap2));
	}

    private static TriangleGrid layout(InputQuad... inputs) {
        double[] xsplits = new double[inputs.length * 2];
        double[] ysplits = new double[inputs.length * 2];

        int t = 0;
		for (InputQuad input : inputs) {
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
			double y1 = ysplits[yi+1];

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
		coords.put((float)x); coords.put((float)y);
	}

    private static void glDrawArrayTexcoord(FloatBuffer coords, double x, double y, InputQuad input) {
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

    public int getVerticesPerRow() {
        return verticesPerRow;
    }

    public void getVertices(int row, FloatBuffer out, int outStride) {
        int src = row * verticesPerRow * 2;
        int dst = out.position();
		for (int n = 0; n < verticesPerRow; n++) {
            out.put(dst++, pos[src++]);
            out.put(dst++, pos[src++]);
            dst += outStride;
		}
	}

    public void getTexCoords(int texIndex, int row, FloatBuffer out, int outStride) {
        int src = row * verticesPerRow * 2;
        int dst = out.position();
        for (int n = 0; n < verticesPerRow; n++) {
            out.put(dst++, tex[texIndex][src++]);
            out.put(dst++, tex[texIndex][src++]);
            dst += outStride;
        }
	}
	public int getRows() {
		return rows;
	}
	public int getCols() {
		return cols;
	}
	public int getTextures() {
		return tex.length;
	}

    public VertexAttributes getVertexAttributes() {
        List<VertexAttribute> list = Lists.newArrayList();
        list.add(new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE));
        list.add(new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
        for (int t = 0; t < getTextures(); t++) {
            list.add(new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + t));
        }
        return new VertexAttributes(list.toArray(new VertexAttribute[list.size()]));
    }

    private static class InputQuad {

        public final Area2D bounds;
        public final Area2D texBounds;
        public final TextureWrap wrap;

        public InputQuad(Area2D bounds, Area2D texBounds, TextureWrap wrap) {
            this.bounds = bounds;
            this.texBounds = texBounds;
            this.wrap = wrap;
        }

    }

}
