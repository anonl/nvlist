package nl.weeaboo.vn.core;

public final class Interpolators {

	public static final IInterpolator LINEAR = new LinearInterpolator();
	public static final IInterpolator BUTTERWORTH = new ButterworthInterpolator();
	public static final IInterpolator HERMITE = new HermiteInterpolator();

	public static final IInterpolator SMOOTH = HERMITE;

	private Interpolators() {
	}

	//Inner Classes
	private static class LinearInterpolator implements IInterpolator {

		private static final long serialVersionUID = 1L;

		@Override
		public float remap(float x) {
			return x;
		}
	}

	private static class ButterworthInterpolator implements IInterpolator {

		private static final long serialVersionUID = 1L;

		@Override
		public float remap(float x) {
			if (x >= .5f) {
				x = 1f - x;
				return -1.5f + 2.5f / (1f + x * x);
			}
			return  2.5f - 2.5f / (1f + x * x);
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
