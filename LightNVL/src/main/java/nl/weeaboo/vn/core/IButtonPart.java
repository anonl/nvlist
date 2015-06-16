package nl.weeaboo.vn.core;

import java.util.Collection;

import nl.weeaboo.vn.script.IScriptFunction;

public interface IButtonPart {

	/**
	 * Buttons store internally if they've been pressed. This method returns if
	 * that has been the case and clears the internal pressed flag.
	 */
	public boolean consumePress();

	/**
	 * Adds a list of global hotkeys that may be used to activate the button.
	 */
	public void addActivationKeys(int... key);

	/**
	 * Removes some keys from the internal list of activation keys.
	 * @see #addActivationKeys(int...)
	 */
	public void removeActivationKeys(int... key);

	/**
	 * Clears the mouse armed state.
	 */
	public void cancelMouseArmed();

	/**
	 * @return <code>true</code> if the mouse is hovering over this button.
	 */
	public boolean isRollover();

	/**
	 * @return <code>true</code> if this button is currently being pressed.
	 */
	public boolean isPressed();

	/**
	 * @return <code>true</code> if this button is enabled.
	 */
	public boolean isEnabled();

	/**
	 * @return <code>true</code> if {@link #setSelected(boolean)} has been used
	 *         to manually set the selected state, or if this button is a toggle
	 *         button and is currently toggled (pressed).
	 */
	public boolean isSelected();

	/**
	 * @return <code>true</code> if this button is a toggle button.
	 */
	public boolean isToggle();

	/**
	 * @return <code>true</code> if this button is considered to have keyboard
	 *         focus and can thus be activated by pressing the confirm
	 *         keyboard/joypad button.
	 */
	public boolean isKeyboardFocus();

	/**
	 * @return The current list of global activation keys.
	 * @see #addActivationKeys(int...)
	 */
	public Collection<Integer> getActivationKeys();

    /**
     * @see #setAlphaEnableThreshold(double)
     */
    public double getAlphaEnableThreshold();

    /**
     * @see #setClickHandler(IScriptFunction)
     */
    public IScriptFunction getClickHandler();

	/**
	 * Enables or disables the button. A disabled button can't be pressed.
	 */
	public void setEnabled(boolean e);

	/**
	 * Changes the selected state for this button. For toggle buttons, the
	 * selected state determines whether the buttons stays pressed or not.
	 */
	public void setSelected(boolean s);

	/**
	 * Changes if this button functions as a regular button or a toggle button
	 * (stays selected when pressed).
	 */
	public void setToggle(boolean t);

	/**
	 * @see #isKeyboardFocus()
	 */
	public void setKeyboardFocus(boolean f);

    /**
     * Changes the alpha enable threshold. When the alpha of this button is
     * below the specified threshold, it will not respond to presses.
     */
    public void setAlphaEnableThreshold(double ae);

    /**
     * Sets a script function that should be called when this button is clicked.
     */
    public void setClickHandler(IScriptFunction func);

}
