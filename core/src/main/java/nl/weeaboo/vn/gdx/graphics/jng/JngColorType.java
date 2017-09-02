package nl.weeaboo.vn.gdx.graphics.jng;

enum JngColorType {

    /** Gray (Y) **/
    GRAY(8),

    /** Color (YCbCr) */
    COLOR(10),

    /** Gray-alpha (Y-alpha) */
    GRAY_ALPHA(12),

    /** Color-alpha (YCbCr-alpha) */
    COLOR_ALPHA(14);

    private final int intValue;

    JngColorType(int ival) {
        intValue = ival;
    }

    public int intValue() {
        return intValue;
    }

    public static JngColorType fromInt(int ival) {
        for (JngColorType colorType : values()) {
            if (colorType.intValue == ival) {
                return colorType;
            }
        }
        throw new IllegalArgumentException("Unsupported ival: " + ival);
    }

    public boolean hasAlpha() {
        switch (this) {
        case GRAY_ALPHA:
        case COLOR_ALPHA:
            return true;
        case GRAY:
        case COLOR:
            return false;
        }

        throw new UnsupportedOperationException("Not implemented for " + this);
    }

}
