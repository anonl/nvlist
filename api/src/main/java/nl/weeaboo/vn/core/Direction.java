package nl.weeaboo.vn.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public int intValue() {
        return intValue;
    }

    public static Direction fromInt(int val) {
        for (Direction dir : values()) {
            if (dir.intValue == val) {
                return dir;
            }
        }

        LOG.warn("Invalid direction: {}", val);
        return NONE;
    }

    public boolean isTop() {
        return this == TOP_LEFT || this == TOP || this == TOP_RIGHT;
    }

    public static boolean containsTop(Iterable<Direction> dirs) {
        return Iterables.any(dirs, Direction::isTop);
    }

    public boolean isRight() {
        return this == TOP_RIGHT || this == RIGHT || this == BOTTOM_RIGHT;
    }

    public static boolean containsRight(Iterable<Direction> dirs) {
        return Iterables.any(dirs, Direction::isRight);
    }

    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM || this == BOTTOM_RIGHT;
    }

    public static boolean containsBottom(Iterable<Direction> dirs) {
        return Iterables.any(dirs, Direction::isBottom);
    }

    public boolean isLeft() {
        return this == TOP_LEFT || this == LEFT || this == BOTTOM_LEFT;
    }

    public static boolean containsLeft(Iterable<Direction> dirs) {
        return Iterables.any(dirs, Direction::isLeft);
    }

}
