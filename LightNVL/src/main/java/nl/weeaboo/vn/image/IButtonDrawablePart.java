package nl.weeaboo.vn.image;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.core.IRenderable;

public interface IButtonDrawablePart extends IRenderable {

    /**
     * Checks if the specified X/Y point lies 'inside' this button for the
     * purpose of mouse/touch processing.
     *
     * @param cx The X-coordinate of the point to test.
     * @param cy The Y-coordinate of the point to test.
     */
    public boolean contains(double cx, double cy);

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

	public double getTextWidth();
	public double getTextHeight();

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
	 * @param a The anchor, uses numpad number positions as directions
	 * @deprecated Use {@link #setVerticalAlign(double)} instead
	 */
	@Deprecated
	public void setTextAnchor(int a);

	/**
	 * Sets the relative position of the text within the button's bounds
	 *
	 * @param valign Relative vertical position for the text: <code>0.0</code>
	 *        is the top, <code>1.0</code> the bottom.
	 */
	public void setVerticalAlign(double valign);

}
