package nl.weeaboo.vn.math;

import java.io.Serializable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;

/**
 * Polygon shape.
 */
public class Polygon implements IShape, Serializable {

    private static final long serialVersionUID = 1L;

    private final int points;
    private final double[] pointsX;
    private final double[] pointsY;
    private final Rect2D bounds;

    public Polygon(double... coords) {
        this(xcoords(coords), ycoords(coords));
    }

    public Polygon(double[] x, double[] y) {
        pointsX = x.clone();
        pointsY = y.clone();

        points = Math.min(x.length, y.length);
        for (int n = 0; n < points; n++) {
            Checks.checkRange(pointsX[n], "p[" + n + "].x");
            Checks.checkRange(pointsY[n], "p[" + n + "].y");
        }

        bounds = calculateBounds(pointsX, pointsY);
    }

    /**
     * Applies a transform to an axis-aligned rectangle.
     *
     * @return A polygon representatopn of the transformed rectangle.
     */
    public static Polygon transformedRect(Matrix transform, Rect2D r) {
        Vec2 p0 = transform.transform(r.x,       r.y      );
        Vec2 p1 = transform.transform(r.x + r.w, r.y      );
        Vec2 p2 = transform.transform(r.x + r.w, r.y + r.h);
        Vec2 p3 = transform.transform(r.x,       r.y + r.h);

        return new Polygon(new double[] { p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y });
    }

    private static double[] xcoords(double[] coords) {
        // Round up in case an odd number of points is given
        double[] result = new double[(coords.length + 1) / 2];
        for (int d = 0, s = 0; d < result.length; d++, s += 2) {
            result[d] = coords[s];
        }
        return result;
    }

    private static double[] ycoords(double[] coords) {
        double[] result = new double[coords.length / 2];
        for (int d = 0, s = 1; d < result.length; d++, s += 2) {
            result[d] = coords[s];
        }
        return result;
    }

    /**
     * Calculates the bounding box for the given set of input points.
     *
     * @param pointsX X-coordinates for the points.
     * @param pointsY Y-coordinates for the points.
     */
    private static Rect2D calculateBounds(double[] pointsX, double[] pointsY) {
        Checks.checkArgument(pointsX.length == pointsY.length, "Arrays must be the same length: pointsX="
                + pointsX.length + ", pointsY=" + pointsY.length);

        if (pointsX.length == 0) {
            return Rect2D.EMPTY;
        }

        double x0 = Double.POSITIVE_INFINITY;
        double y0 = Double.POSITIVE_INFINITY;
        double x1 = Double.NEGATIVE_INFINITY;
        double y1 = Double.NEGATIVE_INFINITY;

        int pointsCount = Math.min(pointsX.length, pointsY.length);
        for (int n = 0; n < pointsCount; n++) {
            x0 = Math.min(x0, pointsX[n]);
            y0 = Math.min(y0, pointsY[n]);
            x1 = Math.max(x1, pointsX[n]);
            y1 = Math.max(y1, pointsY[n]);
        }

        return Rect2D.of(x0, y0, x1 - x0, y1 - y0);
    }

    @Override
    public boolean contains(double x, double y) {
        if (points <= 2 || !bounds.contains(x, y)) {
            return false; // Polygon is a point, line, or bounding rect doesn't intersect
        }

        // Winding counter algorithm
        int windingCounter = 0;
        for (int i0 = 0; i0 < points; i0++) {
            int i1 = (i0 + 1 < points ? i0 + 1 : 0);
            double py1 = pointsY[i1];

            if (pointsY[i0] <= y) {
                if (py1 > y && getPointSide(i0, i1, x, y) < 0) {
                    windingCounter++;
                }
            } else {
                if (py1 <= y && getPointSide(i0, i1, x, y) > 0) {
                    windingCounter--;
                }
            }
        }
        return windingCounter != 0;
    }

    /**
     * @return The side on which the point lies in relation to the line segment between {@code p[i0] -> p[i1]}:
     *     <ul>
     *     <li>-1: Left side
     *     <li>+1: Right side
     *     <li>0: On the line
     *     </ul>
     */
    private int getPointSide(int i0, int i1, double x, double y) {
        double dx = pointsX[i1] - pointsX[i0];
        double dy = pointsY[i1] - pointsY[i0];

        double side = dx * (y - pointsY[i0]) - (x - pointsX[i0]) * dy;
        if (side < 0) {
            return 1;
        } else if (side > 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public Rect2D getBoundingRect() {
        return bounds;
    }

}
