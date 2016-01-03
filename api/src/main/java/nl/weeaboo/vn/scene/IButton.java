package nl.weeaboo.vn.scene;

import java.util.Collection;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.script.IScriptFunction;

public interface IButton extends ITransformable {

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

    /**
     * @see #setDefaultStyle(TextStyle)
     */
    public void extendDefaultStyle(TextStyle ts);

    /**
     * @see #setTouchMargin(double)
     */
    public double getTouchMargin();

    /**
     * @see #setText(StyledText)
     */
    public StyledText getText();

    /**
     * @see #setDefaultStyle(TextStyle)
     */
    public TextStyle getDefaultStyle();

    public ITexture getNormalTexture();
    public ITexture getRolloverTexture();
    public ITexture getPressedTexture();
    public ITexture getPressedRolloverTexture();
    public ITexture getDisabledTexture();
    public ITexture getDisabledPressedTexture();

    public void setNormalTexture(ITexture i);
    public void setRolloverTexture(ITexture i);
    public void setPressedTexture(ITexture i);
    public void setPressedRolloverTexture(ITexture i);
    public void setDisabledTexture(ITexture i);
    public void setDisabledPressedTexture(ITexture i);

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
    public void setText(String text); //Calls setText(StyledText)

    /**
     * Sets the text displayed on top of this button.
     */
    public void setText(StyledText stext);

    /**
     * Sets the default text style to use as a base for the text displayed on
     * top of this button.
     */
    public void setDefaultStyle(TextStyle style);

    /**
     * Sets the relative position of the text within the button's bounds
     *
     * @param valign Relative vertical position for the text: <code>0.0</code>
     *        is the top, <code>1.0</code> the bottom.
     */
    public void setVerticalAlign(double valign);

    /**
     * Checks if the specified X/Y point lies 'inside' this button for the
     * purpose of mouse/touch processing.
     *
     * @param cx The X-coordinate of the point to test.
     * @param cy The Y-coordinate of the point to test.
     */
    @Override
    public boolean contains(double cx, double cy);

    public double getTextWidth();
    public double getTextHeight();
}
