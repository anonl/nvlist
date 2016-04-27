package nl.weeaboo.vn.text.impl;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.TextElement;

final class BasicTextElement extends TextElement {

    private final CharSequence text;
    private final TextStyle style;
    private final float ascent;

    public BasicTextElement(CharSequence text, TextStyle style, int bidiLevel) {
        super(style.getAlign(), bidiLevel);

        this.text = text;
        this.style = style;
        this.ascent = style.getFontSize();

        // Assume momospaced font with square characters
        setLayoutWidth(ascent * text.length());
        setLayoutHeight(ascent);
    }

    public CharSequence getText() {
        return text;
    }

    @Override
    public int getGlyphId(int glyphIndex) {
        // Doesn't support surrogate pairs
        return text.charAt(glyphIndex);
    }

    @Override
    public int getGlyphCount() {
        return text.length();
    }

    @Override
    public TextStyle getGlyphStyle(int glyphIndex) {
        return style;
    }

    @Override
    public float getAscent() {
        return ascent;
    }

}
