package nl.weeaboo.vn.gdx.res;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.common.Checks;

/**
 * Calls the {@link Disposable#dispose()} on resources when they become eligible for garbage collection.
 */
public final class GdxCleaner {

    private static final GdxCleaner INSTANCE = new GdxCleaner();

    private final ReferenceQueue<Object> garbage = new ReferenceQueue<>();
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

        registered.add(new Cleanable(referent, garbage, cleanup));
    }

    /**
     * Returns the number of registered resources. When a resource is disposed, it's also automatically
     * unregistered.
     */
    public int size() {
        return registered.size;
    }

    /** Garbage collect resources that are no longer referenced. */
    public void cleanUp() {
        // Remove dead references
        for (Iterator<Cleanable> itr = registered.iterator(); itr.hasNext(); ) {
            Cleanable ref = itr.next();
            if (ref.get() == null) {
                itr.remove();
            }
        }

        // Clean garbage
        Reference<?> rawReference;
        while ((rawReference = garbage.poll()) != null) {
            ((Cleanable)rawReference).cleanup.dispose();
        }
    }

    private static final class Cleanable extends WeakReference<Object> {

        private final Disposable cleanup;

        public Cleanable(Object referent, ReferenceQueue<? super Object> q, Disposable cleanup) {
            super(referent, q);

            this.cleanup = Checks.checkNotNull(cleanup);
            Checks.checkArgument(referent != cleanup,
                    "Cleanup  function shouldn't reference (or be equal to) the referent");
        }

    }
}
