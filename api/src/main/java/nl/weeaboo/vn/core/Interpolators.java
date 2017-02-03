package nl.weeaboo.vn.core;

public final class Interpolators {

    public static final IInterpolator LINEAR = new LinearInterpolator();
    public static final IInterpolator HERMITE = new HermiteInterpolator();

    public static final IInterpolator SMOOTH = HERMITE;

    private Interpolators() {
    }

    private static class LinearInterpolator implements IInterpolator {

        private static final long serialVersionUID = 1L;

        @Override
        public float remap(float x) {
            return x;
        }
    }

    private static class HermiteInterpolator implements IInterpolator {

        private static final long serialVersionUID = 1L;

        @Override
        public float remap(float x) {
            return x * x * (3 - 2 * x);
        }

    }

}
