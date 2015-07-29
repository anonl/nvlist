package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

public interface IFontStore {

    /**
     * Finds most appropriate font data for the given text style.
     *
     * @return The best match (may not be an exact match).
     */
    IFontMetrics getFontMetrics(TextStyle style);

}
