package nl.weeaboo.common;

import java.io.Serializable;

public final class Area implements Serializable {

	private static final long serialVersionUID = 1L;

	public static Area EMPTY = new Area(0, 0, 0, 0);

	public final int x, y, w, h;

	private Area(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public static Area of(int x, int y, int w, int h) {
		if (x == 0 && y == 0 && w == 0 && h == 0) {
			return EMPTY;
		}
		return new Area(x, y, w, h);
	}

	@Override
	public int hashCode() {
		return ((x<<16) ^ y) ^ ((w<<16) ^ h);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Area) {
			Area a = (Area)obj;
			return x == a.x && y == a.y && w == a.w && h == a.h;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Area(" + x + ", " + y + ", " + w + ", " + h + ")";
	}

	public Area2D toArea2D() {
		return Area2D.of(x, y, w, h);
	}

    public Area flipped(boolean horizontal, boolean vertical) {
        int nx = x, ny = y, nw = w, nh = h;
        if (horizontal) {
            nx += w;
            nw = -nw;
        }
        if (vertical) {
            ny += h;
            nh = -nh;
        }
        return Area.of(nx, ny, nw, nh);
    }

}
