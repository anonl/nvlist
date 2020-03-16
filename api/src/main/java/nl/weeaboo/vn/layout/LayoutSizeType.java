package nl.weeaboo.vn.layout;

/**
 * Layout algorithms calculate multiple sizes for each element: minimum, maximum and preferred.
 */
public enum LayoutSizeType {

    /** Minimum size. */
    MIN,

    /** Preferred or intrinsic size. */
    PREF,

    /** Maximum size. */
    MAX;
}
