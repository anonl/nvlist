package nl.weeaboo.common;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class StringUtil {
	
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	
	private StringUtil() {
	}
	
	/** Calls {@link String#format(Locale, String, Object...)} with {@link Locale#ROOT} */
	public static String formatRoot(String format, Object... args) {
		return String.format(Locale.ROOT, format, args);
	}

    public static String fromUTF8(byte b[]) {
        return fromUTF8(b, 0, b.length);
    }
	public static String fromUTF8(byte b[], int off, int len) {
		return new String(b, off, len, UTF_8);
	}

	public static byte[] toUTF8(String string) {
		return string.getBytes(UTF_8);
	}

	/** @return {@code true} if the given string is empty or contains only whitespace characters */
	public static boolean isWhitespace(String text) {
        int n = 0;
        while (n < text.length()) {
            int c = text.codePointAt(n);
            if (!Character.isWhitespace(c)) {
                return false;
            }
            n += Character.charCount(c);
        }
        return true;
    }
	
	public static String formatMemoryAmount(long bytes) {
		if (bytes < 0) {
			throw new IllegalArgumentException("Negative values not supported: " + bytes);
		}

		final int KiB = 1 << 10;
		final int MiB = 1 << 20;
		final int GiB = 1 << 30;
		
		if (bytes < KiB) {
			return bytes + "B";
		} else if (bytes < MiB) {
			return StringUtil.formatRoot("%.2fKiB", bytes / (double)KiB);
		} else if (bytes < GiB) {
			return StringUtil.formatRoot("%.2fMiB", bytes / (double)MiB);
		} else {
			return StringUtil.formatRoot("%.2fGiB", bytes / (double)GiB);
		}
	}
	
	public static String formatTime(long t, TimeUnit unit) {
		long nanos = TimeUnit.NANOSECONDS.convert(t, unit);
		if (unit.compareTo(TimeUnit.SECONDS) < 0 && nanos > Long.MIN_VALUE && nanos < Long.MAX_VALUE) {
			if (nanos < 1000L) {
				return nanos + "ns";
			} else if (nanos < 1000000L) {
				return StringUtil.formatRoot("%.2fμs", nanos * .001);
			} else if (nanos < 1000000000L) {
				return StringUtil.formatRoot("%.2fms", nanos * .000001);
			}
		}

		long seconds = TimeUnit.SECONDS.convert(t, unit);
		if (seconds < 60) {
			return StringUtil.formatRoot("%.2fs", nanos * .000000001);
		} else if (seconds < 3600) {
			return StringUtil.formatRoot("%dm:%02ds", seconds/60, seconds%60);
		} else {
			return StringUtil.formatRoot("%02dh:%02dm", seconds/3600, (seconds%3600)/60);
		}
	}
	
	/**
	 * Variant of {@link String#replace(CharSequence, CharSequence)}, but with with multiple replacement strings.
	 */
	public static String replaceAll(String string, String[] from, String[] to) {
		if (from.length != to.length) {
			throw new IllegalArgumentException("from.length(" + from.length + ") != to.length(" + to.length + ")");
		}
		
		StringBuilder sb = new StringBuilder();
		
		int pos = 0;
		int len = string.length();
		while (pos < len) {
			int x;
			for (x = 0; x < from.length; x++) {
				if (string.regionMatches(pos, from[x], 0, from[x].length())) {
					sb.append(to[x]);
					pos += from[x].length();
					break;
				}
			}
			if (x >= from.length) {
				sb.append(string.charAt(pos));
				pos++;
			}			
		}
		
		return sb.toString();
	}	
	
}
