package nl.weeaboo.vn.gdx.graphics.jng;

enum PngColorType {

    /**
     * Grayscale
     */
    GRAYSCALE(0),

    /**
     * Red/Green/Blue
     */
    RGB(2),

    /**
     * Palette
     */
    INDEXED(3),

    /**
     * Grayscale with alpha
     */
    GRAYSCALE_AND_ALPHA(4),

    /**
     * Red/Green/Blue/Alpha
     */
    RGBA(6);

    private final int intValue;

    PngColorType(int intValue) {
        this.intValue = intValue;
    }

    public int toInt() {
        return intValue;
    }

}