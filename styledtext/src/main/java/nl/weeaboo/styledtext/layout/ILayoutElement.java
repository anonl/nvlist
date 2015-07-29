package nl.weeaboo.styledtext.layout;

public interface ILayoutElement {

    float getX();
    float getY();

    /** Horizontal advance */
    float getLayoutWidth();

    /** Line height */
    float getLayoutHeight();

    boolean isWhitespace();

}
