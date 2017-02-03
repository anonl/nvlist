package nl.weeaboo.vn.scene;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.script.IScriptFunction;

public interface IButton extends ITransformable {

    /**
     * @see IButtonModel#consumePress()
     */
    public boolean consumePress();

    /**
     * @see IButtonModel#isRollover()
     */
    public boolean isRollover();

    /**
     * @see IButtonModel#isPressed()
     */
    public boolean isPressed();

    /**
     * @see IButtonModel#isEnabled()
     */
    public boolean isEnabled();

    /**
     * @see IButtonModel#setEnabled(boolean)
     */
    public void setEnabled(boolean e);

    /**
     * @see IButtonModel#isSelected()
     */
    public boolean isSelected();

    /**
     * @see IButtonModel#setSelected(boolean)
     */
    public void setSelected(boolean s);

    /**
     * @see IButtonModel#isToggle()
     */
    public boolean isToggle();

    /**
     * @see IButtonModel#setToggle(boolean)
     */
    public void setToggle(boolean t);

    /**
     * @see #setClickHandler(IScriptFunction)
     */
    public IScriptFunction getClickHandler();

    /**
     * Sets the script function to automatically call when the button is pressed.
     *
     * @param func The click handler function, or {@code null} to disable automatic click handling.
     * @see #consumePress()
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

    /**
     * @see IButtonRenderer#setTexture(ButtonViewState, ITexture)
     */
    public void setTexture(ButtonViewState viewState, ITexture tex);

    /**
     * @see IButtonRenderer#setTexture(ButtonViewState, ITexture)
     */
    public void setTexture(ButtonViewState viewState, INinePatch patch);

    /**
     * @see IButtonRenderer#getVerticalAlign()
     */
    VerticalAlign getVerticalAlign();

    /**
     * @see IButtonRenderer#setVerticalAlign(VerticalAlign)
     */
    void setVerticalAlign(VerticalAlign align);

}
