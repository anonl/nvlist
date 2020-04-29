package nl.weeaboo.vn.impl.core;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.google.common.base.Stopwatch;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.Duration;

/**
 * This class contains various helper methods for logging the durations of operations.
 */
public final class DurationLogger {

    private static final int DEFAULT_WARN_MS = 500;

    private final Logger logger;
    private final Stopwatch stopwatch;

    private long warnLimitMs = DEFAULT_WARN_MS;

    private DurationLogger(Logger logger) {
        this.logger = Checks.checkNotNull(logger);
        this.stopwatch = Stopwatch.createStarted();
    }

    /**
     * Creates a new instance, with the internal timer already started.
     */
    public static DurationLogger createStarted(Logger logger) {
        return new DurationLogger(logger);
    }

    /**
     * Logs the duration that the internal timer has been running.
     */
    public void logDuration(String logFormatString, Object... params) {
        Thread thread = Thread.currentThread();

        // Combine parameters
        Object[] combinedParams = new Object[params.length + 2];
        System.arraycopy(params, 0, combinedParams, 0, params.length);
        combinedParams[params.length] = stopwatch;
        combinedParams[params.length + 1] = thread.getName();

        long loadDurationMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        if (loadDurationMs >= warnLimitMs) {
            logger.warn(logFormatString + " took {} on thread '{}'", combinedParams);
        } else {
            logger.debug(logFormatString + " took {} on thread '{}'", combinedParams);
        }
    }

    /**
     * Sets the threshold for 'long' durations (upgrades the log level to WARN).
     */
    public void setWarnLimit(Duration duration) {
        warnLimitMs = duration.toMillis();
    }

}
