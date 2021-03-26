package nl.weeaboo.vn.impl.text;

import java.util.Collections;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.ITextElement;
import nl.weeaboo.styledtext.layout.ITextLayout;

public class TextLayoutStub implements ITextLayout {

    @Override
    public int getGlyphId(int glyphIndex) {
        return 0;
    }

    @Override
    public int getGlyphCount() {
        return 0;
    }

    @Override
    public TextStyle getGlyphStyle(int glyphIndex) {
        return TextStyle.defaultInstance();
    }

    @Override
    public Iterable<ITextElement> getElements() {
        return Collections.emptyList();
    }

    @Override
    public float getOffsetX() {
        return 0;
    }

    @Override
    public float getOffsetY() {
        return 0;
    }

    @Override
    public float getTextWidth() {
        return 0;
    }

    @Override
    public float getTextHeight() {
        return 0;
    }

    @Override
    public float getTextHeight(int startLine, int endLine) {
        return 0;
    }

    @Override
    public int getGlyphOffset(int line) {
        return 0;
    }

    @Override
    public int getLineCount() {
        return 0;
    }

    @Override
    public ITextLayout getLineRange(int startLine, int endLine) {
        return this;
    }

}
