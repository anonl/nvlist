package nl.weeaboo.vn.core.impl;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IChangeListener;

public class ChangeHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient List<IChangeListener> changeListeners;

    private List<IChangeListener> getChangeListeners() {
        if (changeListeners == null) {
            changeListeners = new CopyOnWriteArrayList<IChangeListener>();
        }
        return changeListeners;
    }

    public final void fireChanged() {
        for (IChangeListener cl : getChangeListeners()) {
            cl.onChanged();
        }
    }

    /**
     * <b>Warning: The change listener is internally marked transient and will therefore be lost upon
     * serialization.</b>
     * <p>
     * The given change listener will be called whenever a property of this bounds helper changes.
     */
    public void addChangeListener(IChangeListener cl) {
        getChangeListeners().add(Checks.checkNotNull(cl));
    }

    public void removeChangeListener(IChangeListener cl) {
        getChangeListeners().remove(cl);
    }

}
