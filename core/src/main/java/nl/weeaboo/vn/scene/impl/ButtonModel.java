package nl.weeaboo.vn.scene.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.IInput;
import nl.weeaboo.vn.core.impl.ChangeHelper;
import nl.weeaboo.vn.scene.IButtonModel;
import nl.weeaboo.vn.scene.IButtonView;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.ScriptException;

public class ButtonModel implements IButtonModel {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(ButtonModel.class);

    private final ChangeHelper changeHelper = new ChangeHelper();

    private boolean enabled = true;
    private boolean selected;
    private boolean toggle;
    private double alphaEnableThreshold = 0.9;

    private boolean rollover;
    private boolean mouseArmed;
    private int pressEvents;

    private IScriptFunction clickHandler;

    protected final void fireChanged() {
        changeHelper.fireChanged();
    }

    protected void onPressed() {
        if (isToggle()) {
            setSelected(!isSelected());
        }
        pressEvents++;

        if (clickHandler != null) {
            try {
                clickHandler.call();
            } catch (ScriptException e) {
                LOG.warn("Error calling click handler", e);
            }
        }
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

        boolean mouseContains = view.contains(input.getMouseX(), input.getMouseY());

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
            consumeInput(input, mouseContains);

            if (mouseArmed && !inputHeld) {
                if (mouseArmed && mouseContains) {
                    onPressed();
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

    private void consumeInput(IInput input, boolean mouseContains) {
        if (mouseContains && input.consumeMouse()) {
            mouseArmed = true;
            fireChanged();
        }
    }

    //Getters
    protected boolean isInputHeld(IInput input) {
        if (input.isMouseHeld(true)) {
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

    @Override
    public IScriptFunction getClickHandler() {
        return clickHandler;
    }

    @Override
    public void setClickHandler(IScriptFunction func) {
        this.clickHandler = func;
    }

}
