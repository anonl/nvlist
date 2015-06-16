package nl.weeaboo.vn.core.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nl.weeaboo.common.Checks;
import nl.weeaboo.entity.Part;
import nl.weeaboo.vn.core.IButtonPart;
import nl.weeaboo.vn.core.IInput;
import nl.weeaboo.vn.core.IInputListener;
import nl.weeaboo.vn.script.IScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptFunction;

public class ButtonPart extends Part implements IButtonPart, IInputListener {

    private static final long serialVersionUID = BaseImpl.serialVersionUID;

    private final ChangeHelper changeHelper = new ChangeHelper();
    private final IScriptEventDispatcher eventDispatcher;

    private boolean rollover;
    private boolean keyArmed, mouseArmed;
    private boolean enabled = true;
    private boolean selected;
    private boolean toggle;
    private boolean keyboardFocus;
    private int pressEvents;
    private Set<Integer> activationKeys = new HashSet<Integer>();
    private double alphaEnableThreshold = 0.9;

    private IScriptFunction clickHandler;

    public ButtonPart(IScriptEventDispatcher eventDispatcher) {
        this.eventDispatcher = Checks.checkNotNull(eventDispatcher);
    }

    //Functions
    protected final void fireChanged() {
        changeHelper.fireChanged();
    }

    @Override
    public void addActivationKeys(int... keys) {
        for (int key : keys) {
            activationKeys.add(key);
        }
    }

    @Override
    public void removeActivationKeys(int... keys) {
        for (int key : keys) {
            if (activationKeys.remove(key)) {
                keyArmed = false;
            }
        }
    }

    protected void onPressed() {
        if (isToggle()) {
            setSelected(!isSelected());
        }
        pressEvents++;

        eventDispatcher.addEvent(clickHandler);
    }

    @Override
    public void cancelMouseArmed() {
        mouseArmed = false;
    }

    @Override
    public boolean consumePress() {
        // We could consume only one press, or let this method return the number
        // of consumed presses or something. Let's just consume all of them for
        // now...
        boolean consumed = (pressEvents > 0);
        pressEvents = 0;

        if (consumed) {
            fireChanged();
        }

        return consumed;
    }

    @Override
    public void handleInput(IInput input, boolean mouseContains) {
        boolean changed = false;

        // TODO LVN-002 How to temporarily disable input handling based on the visibility of the accompanying drawable?
        boolean visibleEnough = true; //drawable.isVisible(alphaEnableThreshold);
        if (!visibleEnough) {
            mouseContains = false;
        }

        boolean inputHeld = isInputHeld(input);
        boolean r = mouseContains && (mouseArmed || keyArmed || !inputHeld);

        if (rollover != r) {
            rollover = r;
            changed = true;
        }

        if (isEnabled() && visibleEnough) {
            consumeInput(input, mouseContains);

            if ((mouseArmed || keyArmed) && !inputHeld) {
                if ((mouseArmed && mouseContains) || keyArmed) {
                    onPressed();
                }
                mouseArmed = keyArmed = false;
                changed = true;
            }
        } else {
            pressEvents = 0;

            if (mouseArmed) {
                mouseArmed = false;
                changed = true;
            }
            if (keyArmed) {
                keyArmed = false;
                changed = true;
            }
        }

        r = mouseContains && (mouseArmed || keyArmed || !inputHeld);
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
            keyArmed = false;
            fireChanged();
        } else if (keyboardFocus && input.consumeConfirm()) {
            mouseArmed = false;
            keyArmed = true;
            fireChanged();
        } else if (!activationKeys.isEmpty()) {
            for (Integer key : activationKeys) {
                if (input.consumeKey(key)) {
                    mouseArmed = false;
                    keyArmed = true;
                    fireChanged();
                    break;
                }
            }
        }
    }

    //Getters
    protected boolean isInputHeld(IInput input) {
        if (input.isMouseHeld(true)) {
            return true;
        }
        if (keyboardFocus && input.isConfirmHeld()) {
            return true;
        }
        if (!activationKeys.isEmpty()) {
            for (Integer key : activationKeys) {
                if (input.isKeyHeld(key, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isRollover() {
        return rollover;
    }

    @Override
    public boolean isPressed() {
        return keyArmed || (rollover && mouseArmed);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<Integer> getActivationKeys() {
        return Collections.unmodifiableSet(activationKeys);
    }

    @Override
    public IScriptFunction getClickHandler() {
        return clickHandler;
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
    public boolean isKeyboardFocus() {
        return keyboardFocus;
    }

    @Override
    public double getAlphaEnableThreshold() {
        return alphaEnableThreshold;
    }

    //Setters
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
    public void setKeyboardFocus(boolean f) {
        if (keyboardFocus != f) {
            keyboardFocus = f;
            if (!keyboardFocus) {
                keyArmed = false;
            }
            fireChanged();
        }
    }

    @Override
    public void setAlphaEnableThreshold(double ae) {
        if (alphaEnableThreshold != ae) {
            alphaEnableThreshold = ae;
            fireChanged();
        }
    }

    @Override
    public void setClickHandler(IScriptFunction func) {
        if (clickHandler != func) {
            clickHandler = func;
            fireChanged();
        }
    }

    /**
     * @see ChangeHelper#setChangeListener(IChangeListener)
     */
    void setChangeListener(IChangeListener cl) {
        changeHelper.setChangeListener(cl);
    }

}
