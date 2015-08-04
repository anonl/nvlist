package nl.weeaboo.styledtext.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.weeaboo.styledtext.TextStyle;

public class CompositeTextLayout implements ITextLayout {

    /**
     * No assumptions are made about the visual layout of the text. These layout elements may span multiple
     * lines, switch directions, etc.
     */
    private final List<Elem> elems = new ArrayList<Elem>();

    private int glyphCount;
    private float minX, minY, maxX, maxY;

    public void add(Collection<ILayoutElement> elems) {
        for (ILayoutElement elem : elems) {
            add(elem);
        }
    }

    public void add(ILayoutElement elem) {
        int len = LayoutUtil.getGlyphCount(elem);
        int start = glyphCount;
        int end = start + len;

        elems.add(new Elem(elem, start, end));
        glyphCount += len;

        minX = Math.min(minX, elem.getX());
        minY = Math.min(minY, elem.getY());
        maxX = Math.max(maxX, elem.getX() + elem.getLayoutWidth());
        maxY = Math.max(maxY, elem.getY() + elem.getLayoutHeight());
    }

    @Override
    public float getTextWidth() {
        return maxX - minX;
    }

    @Override
    public float getTextHeight() {
        return maxY - minY;
    }

    @Override
    public int getGlyphCount() {
        return glyphCount;
    }

    @Override
    public Iterable<ILayoutElement> getElements() {
        List<ILayoutElement> result = new ArrayList<ILayoutElement>();
        for (Elem elem : elems) {
            result.add(elem.elem);
        }
        return result;
    }

    @Override
    public int getGlyphId(int glyphIndex) {
        Elem elem = findByGlyphIndex(glyphIndex);
        if (elem == null) {
            throw new ArrayIndexOutOfBoundsException(glyphIndex);
        }
        return elem.getGlyphId(glyphIndex);
    }

    @Override
    public TextStyle getGlyphStyle(int glyphIndex) {
        Elem elem = findByGlyphIndex(glyphIndex);
        if (elem == null) {
            throw new ArrayIndexOutOfBoundsException(glyphIndex);
        }
        return elem.getGlyphStyle(glyphIndex);
    }

    private Elem findByGlyphIndex(int glyphIndex) {
        for (Elem elem : elems) {
            if (elem.containsGlyph(glyphIndex)) {
                return elem;
            }
        }
        return null;
    }

    private static class Elem {

        public final ILayoutElement elem;
        public final int glyphStart;
        public final int glyphEnd;

        public Elem(ILayoutElement elem, int glyphStart, int glyphEnd) {
            this.elem = elem;
            this.glyphStart = glyphStart;
            this.glyphEnd = glyphEnd;
        }

        /**
         * @param glyphIndex Absolute glyph index
         */
        public boolean containsGlyph(int glyphIndex) {
            return glyphIndex >= glyphStart && glyphIndex < glyphEnd;
        }

        /**
         * @param glyphIndex Absolute glyph index
         */
        public int getGlyphId(int glyphIndex) {
            if (elem instanceof IGlyphSequence) {
                IGlyphSequence seq = (IGlyphSequence)elem;
                return seq.getGlyphId(glyphIndex - glyphStart);
            }
            return 0;
        }

        /**
         * @param glyphIndex Absolute glyph index
         */
        public TextStyle getGlyphStyle(int glyphIndex) {
            if (elem instanceof IGlyphSequence) {
                IGlyphSequence seq = (IGlyphSequence)elem;
                return seq.getGlyphStyle(glyphIndex - glyphStart);
            }
            return null;
        }

    }

}
