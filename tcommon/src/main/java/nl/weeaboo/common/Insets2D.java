package nl.weeaboo.common;

import java.io.Serializable;
import java.util.Locale;

public final class Insets2D implements Serializable {

	private static final long serialVersionUID = 1L;

    public static final Insets2D EMPTY = new Insets2D(0, 0, 0, 0);

	public final double top, right, bottom, left;

    private Insets2D(double top, double right, double bottom, double left) {
		if (top < 0 || right < 0 || bottom < 0 || left < 0) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "Insets must be >= 0, top=%f, right=%f, bottom=%f, left=%f", top, right, bottom, left));
		}

		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

    public static Insets2D of(double pad) {
        return of(pad, pad);
    }
    public static Insets2D of(double vertical, double horizontal) {
        return of(vertical, horizontal, vertical, horizontal);
    }
    public static Insets2D of(double top, double right, double bottom, double left) {
        return new Insets2D(top, right, bottom, left);
    }

	@Override
	public int hashCode() {
		long l = Double.doubleToLongBits(top) ^ Double.doubleToLongBits(right)
			^ Double.doubleToLongBits(bottom) ^ Double.doubleToLongBits(left);
		return (int)(l ^ (l>>32));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Insets2D) {
			Insets2D i = (Insets2D)obj;
			return top == i.top && right == i.right && bottom == i.bottom && left == i.left;
		}
		return false;
	}

	@Override
	public String toString() {
        return String.format(Locale.ROOT, "Insets(%f,%f,%f,%f)", top, right, bottom, left);
	}

}
