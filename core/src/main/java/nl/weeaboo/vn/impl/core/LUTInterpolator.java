package nl.weeaboo.vn.impl.core;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IInterpolator;

public final class LUTInterpolator implements IInterpolator {

    private static final long serialVersionUID = 1L;

    private final float[] values;

    public LUTInterpolator(float[] values) {
        this(values, 0, values.length);
    }

    public LUTInterpolator(float[] arr, int off, int len) {
        values = new float[len];
        System.arraycopy(arr, off, values, 0, len);
    }

    /**
     * Creates a new lookup-table-based interpolator.
     *
     * @param len The number of slots in the lookup table.
     */
    public static LUTInterpolator fromInterpolator(IInterpolator i, int len) {
        Checks.checkRange(len, "len", 1);

        float[] lut = new float[len];
        for (int n = 0; n < len; n++) {
            lut[n] = i.remap(n / (float)(len - 1));
        }
        return new LUTInterpolator(lut);
    }

    @Override
    public float remap(float x) {
        //Clamp x to acceptable range
        final int maxIndex = values.length - 1;
        x = Math.max(0, Math.min(maxIndex, x * maxIndex));

        //Find two nearest values
        int prevIndex = (int)x;
        int nextIndex = Math.min(maxIndex, prevIndex + 1);

        //Get nearest two values and interpolate
        float prev = values[prevIndex];
        float next = values[nextIndex];

        return prev + (next - prev) * (x - prevIndex);
    }

}
