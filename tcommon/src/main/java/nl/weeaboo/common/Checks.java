package nl.weeaboo.common;

public final class Checks {

    private Checks() {
    }

    private static String nameString(String name) {
        return (name != null ? name : "<undefined>");
    }

    public static <T> T checkNotNull(T val) {
        return checkNotNull(val, null);
    }
    public static <T> T checkNotNull(T val, String name) {
        if (val == null) {
            throw new IllegalArgumentException("Invalid value for " + nameString(name) + ": null");
        }
        return val;
    }

    public static void checkArgument(boolean condition, String errorMessage) {
        if (!condition) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkState(boolean condition, String errorMessage) {
        if (!condition) {
            throw new IllegalStateException(errorMessage);
        }
    }

    public static int checkRange(int val, String name, int min) {
        return checkRange(val, name, min, Integer.MAX_VALUE);
    }
    public static int checkRange(int val, String name, int min, int max) {
        if (val < min || val > max) {
            throw new IllegalArgumentException("Invalid value for " + nameString(name) + ": " + val);
        }
        return val;
    }

	public static double checkRange(double val, String name) {
	    return checkRange(val, name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}
	public static double checkRange(double val, String name, double min) {
	    return checkRange(val, name, min, Double.POSITIVE_INFINITY);
	}
	public static double checkRange(double val, String name, double min, double max) {
	    if (Double.isNaN(val) || Double.isInfinite(val) || !(val >= min && val <= max)) {
	        throw new IllegalArgumentException("Invalid value for " + nameString(name) + ": " + val);
	    }
	    return val;
	}
}

