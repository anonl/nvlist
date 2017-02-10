package nl.weeaboo.vn.impl.render.fx;

import java.io.Serializable;

public final class ColorMatrix implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int RR = 0;
    private static final int GG = 5;
    private static final int BB = 10;
    private static final int AA = 15;

    /**
     * r' = rr, rg, rb, ra,
     * g' = gr, gg, gb, ga,
     * b' = br, bg, bb, ba,
     * a' = ar, ag, ab, aa
     */
    private final double[] mul = {
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1,
    };

    /**
     * r' = r + roff
     * g' = g + goff
     * b' = b + boff
     * a' = a + aoff
     */
    private final double[] off = {
        0, 0, 0, 0
    };

    public ColorMatrix() {
    }

    /**
     * Returns a column-major float array version of the multiplication part of this matrix.
     */
    public float[] getGLMatrix() {
        // OpenGL by convention uses column-major matrices
        return new float[] {
                (float)mul[0], (float)mul[4], (float)mul[8], (float)mul[12],
                (float)mul[1], (float)mul[5], (float)mul[9], (float)mul[13],
                (float)mul[2], (float)mul[6], (float)mul[10], (float)mul[14],
                (float)mul[3], (float)mul[7], (float)mul[11], (float)mul[15],
        };
    }

    /**
     * Returns the offset part of this matrix as a 4-element float array (RGBA order).
     *
     * @see #setOffsets(double[])
     */
    public float[] getGLOffset() {
        return new float[] {
                (float)off[0], (float)off[1], (float)off[2], (float)off[3],
        };
    }

    /**
     * Sets the diagonals of the multiplication part (RGBA order).
     */
    public void setDiagonals(double[] rgba) {
        mul[RR] = rgba[0];
        mul[GG] = rgba[1];
        mul[BB] = rgba[2];
        mul[AA] = rgba[3];
    }

    /**
     * Sets the multiplication factors (RGBA order) for the red output.
     */
    public void setRedFactors(double[] factorsRGBA) {
        for (int n = 0; n < 4; n++) {
            mul[0 + n] = factorsRGBA[n];
        }
    }

    /**
     * Sets the multiplication factors (RGBA order) for the green output.
     */
    public void setGreenFactors(double[] factorsRGBA) {
        for (int n = 0; n < 4; n++) {
            mul[4 + n] = factorsRGBA[n];
        }
    }

    /**
     * Sets the multiplication factors (RGBA order) for the blue output.
     */
    public void setBlueFactors(double[] factorsRGBA) {
        for (int n = 0; n < 4; n++) {
            mul[8 + n] = factorsRGBA[n];
        }
    }

    /**
     * Sets the multiplication factors (RGBA order) for the alpha output.
     */
    public void setAlphaFactors(double[] factorsRGBA) {
        for (int n = 0; n < 4; n++) {
            mul[12 + n] = factorsRGBA[n];
        }
    }

    /**
     * Sets the offset part of this matrix (RGBA order).
     * <pre>
     * r' = r + roff
     * g' = g + goff
     * b' = b + boff
     * a' = a + aoff
     * </pre>
     */
    public void setOffsets(double[] offsets) {
        for (int n = 0; n < 4; n++) {
            off[n] = offsets[n];
        }
    }

}
