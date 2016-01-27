package nl.weeaboo.vn.scene.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.impl.TransientListenerSupport;
import nl.weeaboo.vn.scene.IButtonModel;

public class ButtonModel implements IButtonModel {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(ButtonModel.class);

    private final TransientListenerSupport changeListeners = new TransientListenerSupport();

    private boolean enabled = true;
    private boolean selected;
    private boolean toggle;

    private boolean rollover;
    private boolean mouseArmed;
    private int pressEvents;

    protected final void fireChanged() {
        changeListeners.fireListeners();
    }

    protected void onClicked() {
        LOG.debug("Button clicked");

        if (isToggle()) {
            setSelected(!isSelected());
        }
        pressEvents++;
    }

    @Override
    public boolean consumePress() {
        /*
         * We could consume only one press, or let this method return the number of consumed presses or
         * something. Let's just consume all of them for now...
         */
        boolean consumed = (pressEvents > 0);
        pressEvents = 0;

        if (consumed) {
            fireChanged();
        }

        return consumed;
    }

    @Override
    public boolean isRollover() {
        return rollover;
    }

    @Override
    public boolean isPressed() {
        return rollover && mouseArmed;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean isToggle() {
        return toggle;
    }

    @Override
    public void setEnabled(boolean e) {
        if (enabled != e) {
            enabled = e;
            if (!enabled) {
                rollover = false;
            }
            fireChanged();
        }
    }

    @Override
    public void setSelected(boolean s) {
        if (selected != s) {
            selected = s;
            fireChanged();
        }
    }

    @Override
    public void setToggle(boolean t) {
        if (toggle != t) {
            toggle = t;
            fireChanged();
        }
    }

    @Override
    public void setRollover(boolean r) {
        if (rollover != r) {
            rollover = r;
            fireChanged();
        }
    }

    @Override
    public void setPressed(boolean p) {
        if (mouseArmed != p) {
            boolean wasPressed = isPressed();

            mouseArmed = p;

            if (enabled && wasPressed && !isPressed()) {
                onClicked();
            }
        }
    }

}
