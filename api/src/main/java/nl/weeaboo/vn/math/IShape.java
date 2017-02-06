package nl.weeaboo.vn.math;

import nl.weeaboo.common.Rect2D;

public interface IShape {

    /**
     * @return {@code true} if the specified point lies inside this shape.
     */
    boolean contains(double x, double y);

    /**
     * Returns the bounding rectangle that completely encompasses this shape.
     */
    Rect2D getBoundingRect();

}
