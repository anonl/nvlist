package nl.weeaboo.vn.scene;

import javax.annotation.CheckForNull;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.script.IScriptFunction;

/**
 * Clickable button.
 */
public interface IButton extends ITransformable {

    /**
     * @see IButtonModel#consumePress()
     */
    boolean consumePress();

    /**
     * @see IButtonModel#isRollover()
     */
    boolean isRollover();

    /**
     * @see IButtonModel#isPressed()
     */
    boolean isPressed();

    /**
     * @see IButtonModel#isEnabled()
     */
    boolean isEnabled();

    /**
     * @see IButtonModel#setEnabled(boolean)
     */
    void setEnabled(boolean e);

    /**
     * @see IButtonModel#isSelected()
     */
    boolean isSelected();

    /**
     * @see IButtonModel#setSelected(boolean)
     */
    void setSelected(boolean s);

    /**
     * @see IButtonModel#isToggle()
     */
    boolean isToggle();

    /**
     * @see IButtonModel#setToggle(boolean)
     */
    void setToggle(boolean t);

    /**
     * @see #setClickHandler(IScriptFunction)
     */
    IScriptFunction getClickHandler();

    /**
     * Sets the script function to automatically call when the button is pressed.
     *
     * @param func The click handler function, or {@code null} to disable automatic click handling.
     * @see #consumePress()
     */
    void setClickHandler(IScriptFunction func);

    /**
     * @see #setTouchMargin(double)
     */
    double getTouchMargin();

    /**
     * Adds some padding to the area in which mouse/touch presses are considered to be 'inside' the button.
     * The main use for this function is to make buttons easier to press on small touchscreen devices.
     *
     * @param p The amount of padding to add to each side of the button.
     */
    void setTouchMargin(double p);

    /**
     * @see #setText(StyledText)
     */
    StyledText getText();

    /**
     * @see #setText(StyledText)
     */
    void setText(String text); //Calls setText(StyledText)

    /**
     * Sets the text displayed on top of this button.
     */
    void setText(StyledText stext);

    /**
     * @see IButtonRenderer#getTexture(ButtonViewState)
     */
    @CheckForNull
    INinePatch getTexture(ButtonViewState viewState);

    /**
     * @see IButtonRenderer#setTexture(ButtonViewState, ITexture)
     */
    void setTexture(ButtonViewState viewState, ITexture tex);

    /**
     * @see IButtonRenderer#setTexture(ButtonViewState, ITexture)
     */
    void setTexture(ButtonViewState viewState, INinePatch patch);

    /**
     * @see IButtonRenderer#getVerticalAlign()
     */
    VerticalAlign getVerticalAlign();

    /**
     * @see IButtonRenderer#setVerticalAlign(VerticalAlign)
     */
    void setVerticalAlign(VerticalAlign align);

}
