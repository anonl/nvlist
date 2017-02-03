package nl.weeaboo.vn.layout;

import java.io.Serializable;
import java.util.Locale;

import com.google.common.primitives.Doubles;

/** Wrapper around a layout-related size value. Makes encoding of infinite values more explicit. */
public final class LayoutSize implements Serializable {

    private static final long serialVersionUID = 1L;

    public static LayoutSize ZERO = new LayoutSize(0);
    public static LayoutSize UNKNOWN = new LayoutSize(Double.NaN);
    public static LayoutSize INFINITE = new LayoutSize(Double.POSITIVE_INFINITY);

    private final double value;

    private LayoutSize(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Negative sizes aren't allowed: " + value);
        }

        this.value = value;
    }

    public static LayoutSize of(double value) {
        if (value == Double.POSITIVE_INFINITY) {
            return INFINITE;
        } else if (Double.isNaN(value)) {
            return UNKNOWN;
        } else if (value == 0.0) {
            return ZERO;
        }
        return new LayoutSize(value);
    }

    public boolean isInfinite() {
        return Double.isInfinite(value);
    }

    public boolean isUnknown() {
        return Double.isNaN(value);
    }

    public double value() {
        if (!Doubles.isFinite(value)) {
            throw new IllegalStateException("LayoutSize is not convertible to a scalar value: " + this);
        }
        return value;
    }

    /**
     * @return The size value, or {@code defaultValue} if infinite or unknown.
     */
    public double value(double defaultValue) {
        if (!Doubles.isFinite(value)) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public int hashCode() {
        return Doubles.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LayoutSize)) {
            return false;
        }

        LayoutSize other = (LayoutSize)obj;
        if (isUnknown()) {
            return other.isUnknown();
        } else if (isInfinite()) {
            return other.isInfinite();
        } else {
            return value() == other.value(Double.NaN);
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "LayoutSize[%.2f]", value);
    }

    public static LayoutSize min(LayoutSize a, LayoutSize b) {
        if (a.isUnknown()) {
            return b;
        } else if (b.isUnknown()) {
            return a;
        } else if (a.isInfinite()) {
            return b;
        } else if (b.isInfinite()) {
            return a;
        } else {
            return (a.value <= b.value ? a : b);
        }
    }

    public static LayoutSize max(LayoutSize a, LayoutSize b) {
        if (a.isUnknown()) {
            return b;
        } else if (b.isUnknown()) {
            return a;
        } else if (a.isInfinite() || b.isInfinite()) {
            return LayoutSize.INFINITE;
        } else {
            return (a.value() >= b.value() ? a : b);
        }
    }

}
