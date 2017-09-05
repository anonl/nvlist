package nl.weeaboo.vn.gdx.graphics.jng;

enum JngAlphaType {
    JPEG(8),
    PNG(0);

    /**
     * Possible values:
     * <ul>
     * <li> 0: PNG grayscale IDAT format.
     * <li> 8: JNG 8-bit grayscale JDAA format.
     * </ul>
     */
    private final int intValue;

    private JngAlphaType(int intValue) {
        this.intValue = intValue;
    }

    int toInt() {
        return intValue;
    }

    static JngAlphaType fromInt(int intValue) throws JngParseException {
        for (JngAlphaType value : values()) {
            if (value.toInt() == intValue) {
                return value;
            }
        }
        throw new JngParseException("Unsupported compression method: " + intValue);
    }

}