package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

public interface IGlyphSequence {

    int getGlyphId(int glyphIndex);

    int getGlyphCount();

    TextStyle getGlyphStyle(int glyphIndex);

}
