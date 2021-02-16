package nl.weeaboo.vn.gdx.res;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.collect.Iterables;
import com.google.errorprone.annotations.concurrent.GuardedBy;

import nl.weeaboo.common.Checks;

/**
 * Calls the {@link Disposable#dispose()} on resources when they become eligible for garbage collection.
 */
@ThreadSafe
public final class GdxCleaner {

    private static final Logger LOG = LoggerFactory.getLogger(GdxCleaner.class);

    private static final GdxCleaner INSTANCE = new GdxCleaner();

    private final Object stateLock = new Object();

    @GuardedBy("stateLock")
    private final ReferenceQueue<Object> garbage = new ReferenceQueue<>();

    @GuardedBy("stateLock")
    private final Array<Cleanable> registered = new Array<>(false, 8);

    private GdxCleaner() {
    }

    public static GdxCleaner get() {
        return INSTANCE;
    }

    /**
     * Registers a disposable resource with the cleaner. The {@link Disposable#dispose()} method will be
     * called when the object is garbage collected.
     */
    public <T> void register(T referent, Disposable cleanup) {
        cleanUp();

        synchronized (stateLock) {
            registered.add(new Cleanable(referent, garbage, cleanup));
        }
    }

    /**
     * Returns the number of registered resources. When a resource is disposed, it's also automatically
     * unregistered.
     */
    public int size() {
        synchronized (stateLock) {
            return registered.size;
        }
    }

    /** Garbage collect resources that are no longer referenced. */
    public void cleanUp() {
        synchronized (stateLock) {
            // Remove dead references
            Iterables.removeIf(registered, ref -> ref.get() == null);

            // Clean garbage
            Reference<?> rawReference;
            while ((rawReference = garbage.poll()) != null) {
                Cleanable cleanable = (Cleanable)rawReference;
                LOG.debug("Disposing resource: {}", cleanable);
                try {
                    cleanable.cleanup.dispose();
                } catch (RuntimeException e) {
                    LOG.error("Cleanup task threw an exception: {}", cleanable.cleanup, e);
                }
            }
        }
    }

    private static final class Cleanable extends WeakReference<Object> {

        private final Disposable cleanup;
        private final String stringRepresentation;

        public Cleanable(Object referent, ReferenceQueue<? super Object> q, Disposable cleanup) {
            super(referent, q);

            this.cleanup = Checks.checkNotNull(cleanup);
            Checks.checkArgument(referent != cleanup,
                    "Cleanup  function shouldn't reference (or be equal to) the referent");

            stringRepresentation = String.valueOf(referent);
        }

        @Override
        public String toString() {
            return stringRepresentation;
        }

    }
}
