package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

public class ChangeHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient IChangeListener changeListener;

    // Functions
    public final void fireChanged() {
        if (changeListener != null) {
            changeListener.onChanged();
        }
    }

    // Getters

    // Setters

    /**
     * <b>Warning: The change listener is internally marked transient and will therefore be lost upon
     * serialization.</b>
     * <p>
     * The given change listener will be called whenever a property of this bounds helper changes.
     */
    public void setChangeListener(IChangeListener cl) {
        changeListener = cl;
    }

}
