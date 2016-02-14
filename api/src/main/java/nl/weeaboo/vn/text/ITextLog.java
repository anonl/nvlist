package nl.weeaboo.vn.text;

import java.io.Serializable;

import nl.weeaboo.styledtext.StyledText;

public interface ITextLog extends Serializable {

    void clear();

    int getPageCount();
    StyledText getPage(int offset);

    int getPageLimit();
    void setPageLimit(int numPages);

    void setText(StyledText text);
    void appendText(StyledText text);

}
