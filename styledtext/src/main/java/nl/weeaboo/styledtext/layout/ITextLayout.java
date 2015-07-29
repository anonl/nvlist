package nl.weeaboo.styledtext.layout;

/**
 * Represents a (word-wrapped) piece of styled text.
 */
public interface ITextLayout extends IGlyphSequence {

    Iterable<ILayoutElement> getElements();

    float getTextWidth();
    float getTextHeight();

}
