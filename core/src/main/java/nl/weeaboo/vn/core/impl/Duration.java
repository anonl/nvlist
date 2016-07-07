package nl.weeaboo.vn.core.impl;

import java.util.concurrent.TimeUnit;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.core.IDuration;

public final class Duration implements IDuration {

    private static final long serialVersionUID = 1L;

    public static final IDuration ZERO = new Duration(0L);

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
        return new Duration(unit.toMillis(duration));
    }

    @Override
    public long toSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }

    @Override
    public String toString() {
        long seconds = toSeconds();
        return StringUtil.formatRoot("%d:%02d", seconds / 60, seconds % 60);
    }

}
