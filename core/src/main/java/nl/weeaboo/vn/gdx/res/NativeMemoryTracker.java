package nl.weeaboo.vn.gdx.res;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.errorprone.annotations.concurrent.GuardedBy;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;

/**
 * Tracks the lifetime and memory use of objects which use native memory outside the Java heap.
 */
public final class NativeMemoryTracker {

    /** Only include details for large(ish) items in the summary. */
    private static final int LARGE_OBJECT_THRESHOLD_BYTES = 1 << 20;

    private static final NativeMemoryTracker INSTANCE = new NativeMemoryTracker();

    private final Object stateLock = new Object();

    @GuardedBy("stateLock")
    private final Array<NativeRef> alive = new Array<>(false, 8);

    @GuardedBy("stateLock")
    private final ReferenceQueue<Object> garbage = new ReferenceQueue<>();

    @GuardedBy("stateLock")
    private long totalBytes;

    private NativeMemoryTracker() {
    }

    public static NativeMemoryTracker get() {
        return INSTANCE;
    }

    public void register(SpriteBatch batch) {
        register(batch, 0); // TODO: How much native memory does a sprite batch use?
    }

    public void register(Pixmap pixmap) {
        register(pixmap, PixmapUtil.getMemoryUseBytes(pixmap));
    }

    public <T> void register(T object, long nativeMemoryBytes) {
        cleanUp();

        synchronized (stateLock) {
            String allocationTrace = Throwables.getStackTraceAsString(new RuntimeException("allocation tracker"));
            alive.add(new NativeRef(object, garbage, nativeMemoryBytes, allocationTrace));
            totalBytes += nativeMemoryBytes;
        }
    }

    /**
     * Returns an estimate of the total native memory usage of alive objects registered with this
     * {@link NativeMemoryTracker}.
     */
    public long getTotalBytes() {
        synchronized (stateLock) {
            return totalBytes;
        }
    }

    /** Garbage collect resources that are no longer referenced. */
    public void cleanUp() {
        synchronized (stateLock) {
            // Remove dead references
            Iterables.removeIf(alive, ref -> ref.get() == null);

            // Clean garbage
            Reference<?> rawReference;
            while ((rawReference = garbage.poll()) != null) {
                totalBytes -= ((NativeRef)rawReference).bytes;
            }
        }
    }

    /** Returns a summary of the native memory used */
    public String getSummary() {
        cleanUp();

        StringBuilder sb = new StringBuilder();
        synchronized (stateLock) {
            sb.append("Native memory used: ").append(StringUtil.formatMemoryAmount(totalBytes));
            sb.append(" ").append(alive.size).append(" objects");
            for (NativeRef ref : alive) {
                Object referent = ref.get();
                if (referent == null) {
                    continue;
                }
                sb.append("\n  ").append(referent);

                long bytes = ref.getNativeMemoryBytes();
                sb.append(" | ").append(StringUtil.formatMemoryAmount(bytes));
                if (bytes >= LARGE_OBJECT_THRESHOLD_BYTES) {
                    sb.append(" | ").append(ref.getAllocationTrace().trim());
                }
            }
        }
        return sb.toString();
    }

    /**
     * Weak reference to an object using native memory.
     */
    private static final class NativeRef extends WeakReference<Object> {

        private final long bytes;
        private final String allocationStackTrace;

        NativeRef(Object referent, ReferenceQueue<? super Object> q, long bytes, String allocationTrace) {
            super(referent, q);

            this.bytes = bytes;
            this.allocationStackTrace = Checks.checkNotNull(allocationTrace);
        }

        /**
         * An estimate of the amount of native memory used by the referenced object.
         */
        public long getNativeMemoryBytes() {
            return bytes;
        }

        /**
         * A stacktrace of the source code location from where the native memory was allocated.
         */
        public String getAllocationTrace() {
            return allocationStackTrace;
        }
    }

}
