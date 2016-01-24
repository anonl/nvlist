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
		this.tex = new float[tex.length][];
		for (int n = 0; n < tex.length; n++) {
			this.tex[n] = tex[n];
		}

		verticesPerRow = cols * 2;
	}

	//Functions
	public static TriangleGrid layout1(Area2D bounds0, Area2D texBounds0, TextureWrap wrap0) {
		return layout(new Area2D[] {bounds0},
				new Area2D[] {texBounds0},
				new TextureWrap[] {wrap0});
	}
	public static TriangleGrid layout2(Area2D bounds0, Area2D texBounds0, TextureWrap wrap0,
			Area2D bounds1, Area2D texBounds1, TextureWrap wrap1)
	{
		return layout(new Area2D[] {bounds0, bounds1},
				new Area2D[] {texBounds0, texBounds1},
				new TextureWrap[] {wrap0, wrap1});
	}
	public static TriangleGrid layout3(Area2D bounds0, Area2D texBounds0, TextureWrap wrap0,
			Area2D bounds1, Area2D texBounds1, TextureWrap wrap1,
			Area2D bounds2, Area2D texBounds2, TextureWrap wrap2)
	{
		return layout(new Area2D[] {bounds0, bounds1, bounds2},
				new Area2D[] {texBounds0, texBounds1, texBounds2},
				new TextureWrap[] {wrap0, wrap1, wrap2});
	}
	private static TriangleGrid layout(Area2D[] bounds, Area2D[] texBounds, TextureWrap[] wrap) {
		if (bounds.length != texBounds.length) {
			throw new IllegalArgumentException("bounds.length != texBounds.length");
		} else if (texBounds.length != wrap.length) {
			throw new IllegalArgumentException("texBounds.length != wrap.length");
		}

        double[] xsplits = new double[bounds.length * 2];
        double[] ysplits = new double[bounds.length * 2];

        int t = 0;
		for (Area2D r : bounds) {
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
		FloatBuffer[] texs = new FloatBuffer[texBounds.length];
		for (int n = 0; n < texs.length; n++) {
            texs[n] = FloatBuffer.allocate(2 * vertices);
		}

		for (int yi = 0; yi < rows; yi++) {
			double y0 = ysplits[yi];
			double y1 = ysplits[yi+1];

			for (int xi = 0; xi < cols; xi++) {
				double x = xsplits[xi];

                glDrawArrayVertex(pos, x, y1);
                for (int n = 0; n < texs.length; n++) {
                    glDrawArrayTexcoord(texs[n], x, y1, bounds[n], texBounds[n], wrap[n]);
                }

				glDrawArrayVertex(pos, x, y0);
				for (int n = 0; n < texs.length; n++) {
					glDrawArrayTexcoord(texs[n], x, y0, bounds[n], texBounds[n], wrap[n]);
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
	private static void glDrawArrayTexcoord(FloatBuffer coords, double x, double y,
			Area2D bounds, Area2D texBounds, TextureWrap wrap)
	{
		double normalizedX = (x-bounds.x) / bounds.w;
		double normalizedY = (y-bounds.y) / bounds.h;

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

	//Getters
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

}
