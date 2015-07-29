package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

public interface IGlyphSequence {

    int getGlyphCount();

    TextStyle getGlyphStyle(int glyphIndex);

}
