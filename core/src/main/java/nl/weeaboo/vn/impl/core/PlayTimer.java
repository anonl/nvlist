package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import nl.weeaboo.vn.core.Duration;
import nl.weeaboo.vn.core.IPlayTimer;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.save.IStorage;

public class PlayTimer implements IPlayTimer {

    private static final long serialVersionUID = 1L;

    private static final String KEY_TOTAL = "vn.timer.total";

    private static final StaticRef<IInput> inputRef = StaticEnvironment.INPUT;

    private long totalNanos;

    /** If idle for longer than this amount of time, stop incrementing the total play time. */
    private long maxIdleNanos = TimeUnit.SECONDS.toNanos(60);

    /** Increment the total play time by at most this amount at a time. */
    private long maxFrameNanos = TimeUnit.SECONDS.toNanos(1);

    private transient long previousTimestamp;
    private transient long idleNanos;

    @Override
    public void update() {
        long timestamp = now();

        if (previousTimestamp != 0) { // A timestamp of 0 is interpreted as 'unknown'
            long diffNanos = Math.max(0, timestamp - previousTimestamp);

            // Increment idle time
            IInput input = inputRef.get();
            if (input.isIdle()) {
                idleNanos += diffNanos;
            } else {
                idleNanos = 0;
            }

            // Don't increase time when idle for a long time
            if (idleNanos <= maxIdleNanos) {
                // Don't increase time by too much to protect against freezes/hibernate/glitches
                totalNanos += Math.min(maxFrameNanos, diffNanos);
            }
        }

        previousTimestamp = timestamp;
    }

    @Override
    public void load(IStorage storage) throws IOException {
        totalNanos = storage.getLong(KEY_TOTAL, totalNanos);
    }

    @Override
    public void save(IStorage storage) throws IOException {
        storage.setLong(KEY_TOTAL, totalNanos);
    }

    protected long now() {
        return System.nanoTime();
    }

    @Override
    public Duration getTotalPlayTime() {
        return Duration.fromDuration(totalNanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public Duration getIdleTime() {
        return Duration.fromDuration(idleNanos, TimeUnit.NANOSECONDS);
    }

}
