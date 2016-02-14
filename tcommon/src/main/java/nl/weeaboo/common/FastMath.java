package nl.weeaboo.common;

public final class FastMath {

	private FastMath() {
	}

	public static float PI = (float)Math.PI;
	public static float TWO_PI = PI + PI;
	public static float HALF_PI = PI / 2;

	public static float sin(float s) {
		return fastSin(s * fastAngleScale); //High precision
	}
	public static float cos(float s) {
		return fastCos(s * fastAngleScale); //High precision
	}

	public static float acos(float s) {
		return fastArcCos(s) / fastAngleScale;
	}
	public static float asin(float s) {
		return fastArcSin(s) / fastAngleScale;
	}

	public static float fastArcTan2(float dy, float dx) {
		float coeff_1 = SIN_LUT_SIZE >> 3;
		float coeff_2 = 3 * coeff_1;

		float absDy = Math.abs(dy);
		if (absDy == 0.0) {
			absDy = 0.0001f;
		}

		float angle;
		if (dx >= 0) {
			float r = (dx - absDy) / (dx + absDy);
			angle = coeff_1 - r * coeff_1;
		} else {
			float r = (dx + absDy) / (absDy - dx);
			angle = coeff_2 - r * coeff_1;
		}

		if (dy < 0) {
			angle = -angle; //Negate if in quad 3 or 4
		}

		angle += (SIN_LUT_SIZE >> 2);
		if (angle < 0) {
			angle += SIN_LUT_SIZE;
		}
		return angle;
	}

	public static boolean isPowerOfTwo(int w, int h) {
		return isPowerOfTwo(w) && isPowerOfTwo(h);
	}
	public static boolean isPowerOfTwo(int sz) {
		return sz > 0 && (sz & (sz-1)) == 0;
	}
	public static int toPowerOfTwo(int target) {
        if (target <= 0) {
            throw new IllegalArgumentException("target must be positive");
        }
        if (target > 0x40000000) {
            throw new IllegalArgumentException("No greater power-of-two possible in 32 bits: " + target);
        }

		int cur = (target < 16 ? 1 : 16); //Start with a valid power of two lower than target
		while (cur < target) {
			cur <<= 1; //Double cur until at least as large as target
		}
		return cur;
	}
	public static Dim toPowerOfTwo(int w, int h) {
		return new Dim(toPowerOfTwo(w), toPowerOfTwo(h));
	}

	public static int align(int val, int align) {
		if (!isPowerOfTwo(align)) {
			throw new IllegalArgumentException("Alignment must be a power of two");
		}
		return (val+align-1) & ~(align-1);
	}

	//-------------------------------------------------------------------------
	//--- LUT implementations of trig functions -------------------------------
	//-------------------------------------------------------------------------

	private static float SIN_LUT[];
	private static int SIN_LUT_SIZE = 512;
	private static int SIN_LUT_MASK = 511;
	public static float fastAngleScale = SIN_LUT_SIZE / (TWO_PI);

	private synchronized static void initSinLUT() {
		if (SIN_LUT != null) {
			return;
		}

		double s = Math.PI / (SIN_LUT_SIZE>>1);

		SIN_LUT = new float[SIN_LUT_SIZE];
		for (int n = 0; n < SIN_LUT_SIZE; n++) {
			SIN_LUT[n] = (float)Math.sin(n * s);
		}
	}

	public static float fastSin(int angle) {
		if (SIN_LUT == null) {
			initSinLUT();
		}
		return SIN_LUT[angle & SIN_LUT_MASK];
	}
	public static float fastCos(int angle) {
		if (SIN_LUT == null) {
			initSinLUT();
		}
		return SIN_LUT[(angle + (SIN_LUT_SIZE>>2)) & SIN_LUT_MASK];
	}

	private static int floor(float angle) {
		return (angle >= 0 ? (int)(angle) : (int)(angle - 1));
	}

	public static float fastSin(float angle) {
		int a = floor(angle);
		float prev = fastSin(a);
		float next = fastSin(a + 1);

		float result = prev + (next-prev) * Math.abs(angle - a);
		return result;
	}
	public static float fastCos(float angle) {
		return fastSin(angle + (SIN_LUT_SIZE>>2));
	}

	private static float ASIN_LUT[];
	private static int ASIN_LUT_SIZE = 1024;

	protected synchronized static void initASinLUT() {
		if (ASIN_LUT != null) {
			return;
		}

		int halfLutSize = ASIN_LUT_SIZE>>1;

		ASIN_LUT = new float[ASIN_LUT_SIZE];
		for (int n = 0; n < ASIN_LUT_SIZE; n++) {
			double d = Math.asin((n - halfLutSize) / (float)halfLutSize);
			ASIN_LUT[n] = (float)(fastAngleScale * d);
		}
	}

	public static float fastArcSin(float a) {
		if (ASIN_LUT == null) {
			initASinLUT();
		}

		if (Float.isNaN(a) || a < -1 || a > 1) return Float.NaN;
		if (a == -0f || a == 0f) return a;

		int halfLutSize = ASIN_LUT_SIZE>>1;
		int index = Math.abs(Math.round(halfLutSize + a * halfLutSize));
		return ASIN_LUT[Math.max(0, Math.min(ASIN_LUT_SIZE-1, index))];
	}

	public static float fastArcCos(float a) {
		return (SIN_LUT_SIZE>>1) - fastArcSin(a);
	}

}
