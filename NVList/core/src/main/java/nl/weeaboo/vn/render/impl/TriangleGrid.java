package nl.weeaboo.vn.render.impl;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.TreeSet;

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

		TreeSet<Double> sorter = new TreeSet<Double>();
		for (Area2D r : bounds) {
			sorter.add(r.x);
			sorter.add(r.x + r.w);
		}
		int t = 0;
		double[] xsplits = new double[sorter.size()];
		for (Double d : sorter) {
			xsplits[t++] = d;
		}

		sorter.clear();
		for (Area2D r : bounds) {
			sorter.add(r.y);
			sorter.add(r.y + r.h);
		}
		t = 0;
		double[] ysplits = new double[sorter.size()];
		for (Double d : sorter) {
			ysplits[t++] = d;
		}

		int cols = xsplits.length;
		int rows = ysplits.length - 1;
		int vertices = (2*cols) * rows + 1;
		int bytes = 2 * 4 * vertices;
		FloatBuffer pos  = FloatBuffer.allocate(bytes);
		FloatBuffer[] texs = new FloatBuffer[texBounds.length];
		for (int n = 0; n < texs.length; n++) {
			texs[n] = FloatBuffer.allocate(bytes);
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

		//System.out.println(bounds + " " + x + "<->" + normalizedX + " " + y + "<->" + normalizedY);

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

		//System.out.printf("x=%.1f, y=%.1f :: u=%.1f, v=%.1f\n", x, y, u, v);
		coords.put((float)u); coords.put((float)v);
	}

	//Getters
	public void getVertices(FloatBuffer out, int row) {
		int offset = row * verticesPerRow * 2;
		for (int n = 0; n < verticesPerRow; n++) {
			out.put(pos[offset++]);
			out.put(pos[offset++]);
		}
	}
	public void getTexCoords(FloatBuffer out, int texIndex, int row) {
		int offset = row * verticesPerRow * 2;
		for (int n = 0; n < verticesPerRow; n++) {
			out.put(tex[texIndex][offset++]);
			out.put(tex[texIndex][offset++]);
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

	//Setters

}
