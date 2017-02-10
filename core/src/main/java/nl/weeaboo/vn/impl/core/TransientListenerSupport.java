package nl.weeaboo.vn.impl.core;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IEventListener;

public class TransientListenerSupport implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient List<IEventListener> transientListeners;

    /**
     * @return The internal (mutable) collection storing the transient listeners.
     */
    private List<IEventListener> transientListeners() {
        if (transientListeners == null) {
            transientListeners = new CopyOnWriteArrayList<>();
        }
        return transientListeners;
    }

    /**
     * Sends an event to all attached listeners.
     */
    public final void fireListeners() {
        for (IEventListener cl : transientListeners()) {
            cl.onEvent();
        }
    }

    /**
     * <b>Warning: The listener is internally marked transient and will therefore be lost upon
     * serialization.</b>
     * <p>
     * The given change listener will be called whenever a property of this bounds helper changes.
     */
    public void addTransientListener(IEventListener cl) {
        transientListeners().add(Checks.checkNotNull(cl));
    }

    /**
     * Removes a previously added listener.
     */
    public void removeTransientListener(IEventListener cl) {
        transientListeners().remove(cl);
    }

}
