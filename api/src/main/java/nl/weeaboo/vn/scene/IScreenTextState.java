package nl.weeaboo.vn.scene;

import java.io.Serializable;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.IUpdateable;
import nl.weeaboo.vn.text.ITextLog;

public interface IScreenTextState extends Serializable, IUpdateable {

    ITextDrawable getTextDrawable();
    void setTextDrawable(ITextDrawable td);

    double getTextSpeed();
    void setTextSpeed(double speed);

    StyledText getText();

    void setText(String s);
    void setText(StyledText st);

    void appendText(String s);
    void appendText(StyledText st);

    void appendTextLog(String s, boolean newPage);
    void appendTextLog(StyledText st, boolean newPage);

    ITextLog getTextLog();

}
