package nl.weeaboo.vn.impl.render;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.google.errorprone.annotations.concurrent.GuardedBy;

/**
 * Logger to be used for code that runs during rendering. Log messages are rate-limited to avoid flooding the
 * log with the same problem every frame.
 */
public final class RenderLog {

    private static final RenderLog INSTANCE = new RenderLog("render");
    private static final long THROTTLE_NANOS = TimeUnit.SECONDS.toNanos(10);

    private final Logger logger;

    private final Object stateLock = new Object();

    @GuardedBy("stateLock")
    private final HashMap<String, Long> lastLogTimestamps = new HashMap<>();

    private RenderLog(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    /** @see Logger#warn */
    public static void warn(String message, Object... args) {
        INSTANCE.log(Level.WARN, message, args);
    }

    /** @see Logger#error */
    public static void error(String message, Object... args) {
        INSTANCE.log(Level.ERROR, message, args);
    }

    private boolean isEnabled(Level level) {
        switch (level) {
        case WARN: return logger.isWarnEnabled();
        case ERROR: return logger.isErrorEnabled();
        default: throw new IllegalArgumentException("Unsupported log level: " + level);
        }
    }

    private void log(Level level, String message, Object... args) {
        if (!isEnabled(level)) {
            return;
        }

        synchronized (stateLock) {
            long now = System.nanoTime();
            Long timestamp = lastLogTimestamps.get(message);
            if (timestamp != null && now < timestamp.longValue() + THROTTLE_NANOS) {
                // This message was logged too recently
                logger.trace("(suppressed) " + message, args);
                return;
            }

            lastLogTimestamps.put(message, now);

            switch (level) {
            case WARN: logger.warn(message, args); break;
            case ERROR: logger.error(message, args); break;
            default: throw new IllegalArgumentException("Unsupported log level: " + level);
            }
        }
    }
}
