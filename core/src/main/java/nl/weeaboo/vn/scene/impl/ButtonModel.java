package nl.weeaboo.vn.scene.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.IInput;
import nl.weeaboo.vn.core.KeyCode;
import nl.weeaboo.vn.core.impl.TransientListenerSupport;
import nl.weeaboo.vn.scene.IButtonModel;
import nl.weeaboo.vn.scene.IButtonView;

public class ButtonModel implements IButtonModel {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(ButtonModel.class);

    private final TransientListenerSupport changeListeners = new TransientListenerSupport();

    private boolean enabled = true;
    private boolean selected;
    private boolean toggle;
    private double alphaEnableThreshold = 0.9;

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
    public void handleInput(IButtonView view, IInput input) {
        boolean changed = false;

        boolean mouseContains = view.contains(input.getPointerX(), input.getPointerY());

        boolean visibleEnough = view.isVisible(alphaEnableThreshold);
        if (!visibleEnough) {
            mouseContains = false;
        }

        boolean inputHeld = isInputHeld(input);
        boolean r = mouseContains && (mouseArmed || !inputHeld);

        if (rollover != r) {
            rollover = r;
            changed = true;
        }

        if (isEnabled() && visibleEnough) {
            if (mouseContains && input.consumePress(KeyCode.MOUSE_LEFT)) {
                mouseArmed = true;
                fireChanged();
            }

            if (mouseArmed && !inputHeld) {
                if (mouseArmed && mouseContains) {
                    onClicked();
                }
                mouseArmed = false;
                changed = true;
            }
        } else {
            pressEvents = 0;

            if (mouseArmed) {
                mouseArmed = false;
                changed = true;
            }
        }

        r = mouseContains && (mouseArmed || !inputHeld);
        if (rollover != r) {
            rollover = r;
            changed = true;
        }

        if (changed) {
            fireChanged();
        }
    }

    //Getters
    protected boolean isInputHeld(IInput input) {
        if (input.isPressed(KeyCode.MOUSE_LEFT, true)) {
            return true;
        }
        return false;
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

}
