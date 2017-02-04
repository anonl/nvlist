package nl.weeaboo.vn.scene;

import java.io.Serializable;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.text.ITextLog;

public interface IScreenTextState extends Serializable, IUpdateable {

    /**
     * @return The main text drawable.
     */
    ITextDrawable getTextDrawable();

    /**
     * Sets the main text drawable.
     */
    void setTextDrawable(ITextDrawable td);

    /**
     * @return The relative text speed.
     * @see #setTextSpeed(double)
     */
    double getTextSpeed();

    /**
     * Sets the relative text speed in the range {@code (0.0, PosInf)}.
     */
    void setTextSpeed(double speed);

    /**
     * Returns the text of the main text drawable.
     */
    StyledText getText();

    /**
     * @see #setText(StyledText)
     */
    void setText(String s);

    /**
     * Changes the text of the main text drawable.
     */
    void setText(StyledText st);

    /**
     * @see #appendText(StyledText)
     */
    void appendText(String s);

    /**
     * Appends additional text to the main text drawable.
     */
    void appendText(StyledText st);

    /**
     * @return This screen's text log.
     */
    ITextLog getTextLog();

}
