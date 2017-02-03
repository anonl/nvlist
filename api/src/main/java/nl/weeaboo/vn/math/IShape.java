package nl.weeaboo.vn.math;

import nl.weeaboo.common.Rect2D;

public interface IShape {

    boolean contains(double x, double y);

    Rect2D getBoundingRect();

}
