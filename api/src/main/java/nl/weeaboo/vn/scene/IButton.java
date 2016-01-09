package nl.weeaboo.vn.scene;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.script.IScriptFunction;

public interface IButton extends ITransformable {

	/**
	 * Buttons store internally if they've been pressed. This method returns if
	 * that has been the case and clears the internal pressed flag.
	 */
	public boolean consumePress();

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
     * Enables or disables the button. A disabled button can't be pressed.
     */
    public void setEnabled(boolean e);

	/**
	 * @return <code>true</code> if {@link #setSelected(boolean)} has been used
	 *         to manually set the selected state, or if this button is a toggle
	 *         button and is currently toggled (pressed).
	 */
	public boolean isSelected();

	/**
     * Changes the selected state for this button. For toggle buttons, the selected state determines whether
     * the buttons stays pressed or not.
     */
	public void setSelected(boolean s);

    /**
     * @return <code>true</code> if this button is a toggle button.
     */
    public boolean isToggle();

	/**
     * Changes if this button functions as a regular button or a toggle button (stays selected when pressed).
     */
	public void setToggle(boolean t);

    /**
     * @see #setClickHandler(IScriptFunction)
     */
    public IScriptFunction getClickHandler();

    /**
     * Sets a script function that should be called when this button is clicked.
     */
    public void setClickHandler(IScriptFunction func);

    /**
     * @see #setTouchMargin(double)
     */
    public double getTouchMargin();

    /**
     * Adds some padding to the area in which mouse/touch presses are considered to be 'inside' the button.
     * The main use for this function is to make buttons easier to press on small touchscreen devices.
     *
     * @param p The amount of padding to add to each side of the button.
     */
    public void setTouchMargin(double p);

    /**
     * @see #setText(StyledText)
     */
    public StyledText getText();

    /**
     * @see #setText(StyledText)
     */
    public void setText(String text); //Calls setText(StyledText)

    /**
     * Sets the text displayed on top of this button.
     */
    public void setText(StyledText stext);

}
