package nl.weeaboo.vn.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public enum Direction {

    NONE(0),
    TOP_LEFT(7),
    TOP(8),
    TOP_RIGHT(9),
    LEFT(4),
    CENTER(5),
    RIGHT(6),
    BOTTOM_LEFT(1),
    BOTTOM(2),
    BOTTOM_RIGHT(3);

    private static final Logger LOG = LoggerFactory.getLogger(Direction.class);

    private final int intValue;

    private Direction(int val) {
        this.intValue = val;
    }

    /**
     * @return A unique integer representation of the direction. This is <em>not</em> the same as {@link #ordinal()}.
     */
    public int intValue() {
        return intValue;
    }

    /**
     * Performs the opposite operation of {@link #intValue()}, converting an int back to a direction.
     */
    public static Direction fromInt(int val) {
        for (Direction dir : values()) {
            if (dir.intValue == val) {
                return dir;
            }
        }

        LOG.warn("Invalid direction: {}", val);
        return NONE;
    }

    /**
     * @return {@code true} if this is one of the TOP directions.
     */
    public boolean isTop() {
        return this == TOP_LEFT || this == TOP || this == TOP_RIGHT;
    }

    /**
     * @return {@code true} if one or more directions are a TOP direction.
     */
    public static boolean containsTop(Iterable<Direction> dirs) {
        // Don't use a method reference, it crashes RoboVM (ArrayIndexOutOfBounds: -1, LambdaClassGenerator.java:203)
        return Iterables.any(dirs, new Predicate<Direction>() {
            @Override
            public boolean apply(Direction d) {
                return Preconditions.checkNotNull(d).isTop();
            }
        });
    }

    /**
     * @return {@code true} if this is one of the RIGHT directions.
     */
    public boolean isRight() {
        return this == TOP_RIGHT || this == RIGHT || this == BOTTOM_RIGHT;
    }

    /**
     * @return {@code true} if one or more directions are a RIGHT direction.
     */
    public static boolean containsRight(Iterable<Direction> dirs) {
        // Don't use a method reference, see #containsTop()
        return Iterables.any(dirs, new Predicate<Direction>() {
            @Override
            public boolean apply(Direction d) {
                return Preconditions.checkNotNull(d).isRight();
            }
        });
    }

    /**
     * @return {@code true} if this is one of the BOTTOM directions.
     */
    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM || this == BOTTOM_RIGHT;
    }

    /**
     * @return {@code true} if one or more directions are a BOTTOM direction.
     */
    public static boolean containsBottom(Iterable<Direction> dirs) {
        // Don't use a method reference, see #containsTop()
        return Iterables.any(dirs, new Predicate<Direction>() {
            @Override
            public boolean apply(Direction d) {
                return Preconditions.checkNotNull(d).isBottom();
            }
        });
    }

    /**
     * @return {@code true} if this is one of the LEFT directions.
     */
    public boolean isLeft() {
        return this == TOP_LEFT || this == LEFT || this == BOTTOM_LEFT;
    }

    /**
     * @return {@code true} if one or more directions are a LEFT direction.
     */
    public static boolean containsLeft(Iterable<Direction> dirs) {
        // Don't use a method reference, see #containsTop()
        return Iterables.any(dirs, new Predicate<Direction>() {
            @Override
            public boolean apply(Direction d) {
                return Preconditions.checkNotNull(d).isLeft();
            }
        });
    }

}
