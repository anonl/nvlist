package nl.weeaboo.vn.core;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;

public final class Duration implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Duration ZERO = new Duration(0L);

    /** Store millisecond precision internally. */
    private final long millis;

    private Duration(long millis) {
        Checks.checkRange(millis, "Duration must be >= 0", 0L);

        this.millis = millis;
    }

    /** Creates a new duration object representing a number of seconds. */
    public static Duration fromSeconds(long seconds) {
        return fromDuration(seconds, TimeUnit.SECONDS);
    }

    /**
     * Creates a new duration object.
     *
     * @param unit Duration only stores millisecond precision. To prevent unintended precision loss, the
     *        {@link TimeUnit#MICROSECONDS} and {@link TimeUnit#NANOSECONDS} units are not accepted by this method.
     */
    public static Duration fromDuration(long duration, TimeUnit unit) {
        if (unit == TimeUnit.MICROSECONDS || unit == TimeUnit.NANOSECONDS) {
            throw new IllegalArgumentException("Duration isn't precise enough to store " + unit);
        }

        if (duration == 0) {
            return ZERO;
        }
        return new Duration(unit.toMillis(duration));
    }

    /**
     * @return The duration in seconds, rounded down.
     */
    public long toSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }

    @Override
    public String toString() {
        long seconds = toSeconds();

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours == 0) {
            return StringUtil.formatRoot("%d:%02d", minutes, seconds);
        } else {
            return StringUtil.formatRoot("%d:%02d:%02d", hours, minutes, seconds);
        }
    }

}
