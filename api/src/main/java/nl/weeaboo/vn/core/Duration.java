package nl.weeaboo.vn.core;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;

public final class Duration implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Duration ZERO = new Duration(0L);

    /** Store millisecond precision internally */
    private final long millis;

    private Duration(long millis) {
        Checks.checkRange(millis, "Duration must be >= 0", 0L);

        this.millis = millis;
    }

    public static Duration fromSeconds(long seconds) {
        return fromDuration(seconds, TimeUnit.SECONDS);
    }
    public static Duration fromDuration(long duration, TimeUnit unit) {
        if (unit == TimeUnit.MICROSECONDS || unit == TimeUnit.NANOSECONDS) {
            throw new IllegalArgumentException("Duration isn't precise enough to store " + unit);
        }

        if (duration == 0) {
            return ZERO;
        }
        return new Duration(unit.toMillis(duration));
    }

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
