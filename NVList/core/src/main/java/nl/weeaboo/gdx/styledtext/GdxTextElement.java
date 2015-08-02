package nl.weeaboo.gdx.styledtext;

import java.util.Arrays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

import nl.weeaboo.styledtext.MirrorChars;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.TextElement;

final class GdxTextElement extends TextElement {

    private final TextStyle style;
    private final BitmapFont font;
    private final float originalScaleX, originalScaleY;
    private final float scaleXY;

    private GlyphLayout glyphLayout;
    private float capHeight;
    private int glyphCount;

    public GdxTextElement(CharSequence str, TextStyle style, int bidiLevel, BitmapFont font) {
        super(style.getAlign(), bidiLevel);

        this.style = style;
        this.font = font;

        originalScaleX = font.getScaleX();
        originalScaleY = font.getScaleY();
        scaleXY = GdxFontMetrics.getScale(style, font);

        initGlyphLayout(str, style);
    }

    private void initGlyphLayout(CharSequence str, TextStyle style) {
        Color color = new Color();
        Color.argb8888ToColor(color, style.getColor());

        applyScale();
        {
            if (isRightToLeft()) {
                // Reverse codepoints for layout if RTL
                StringBuilder sb = new StringBuilder(str).reverse();
                str = mirrorGlyphs(sb);
            }

            glyphLayout = new GlyphLayout(font, str, color, 0, Align.left, false);
            if (glyphLayout.runs.size > 1) {
                throw new IllegalArgumentException(
                        "Arguments result in a layout with multiple glyph runs: " + glyphLayout.runs.size);
            }
            capHeight = font.getCapHeight();

            glyphCount = getGlyphCount(glyphLayout);
            setLayoutWidth(glyphLayout.width);
            setLayoutHeight(glyphLayout.height);
        }
        resetScale();
    }

    private CharSequence mirrorGlyphs(StringBuilder sb) {
        for (int n = 0; n < sb.length(); n++) {
            char original = sb.charAt(n);
            char mirrored = MirrorChars.getMirrorChar(original);
            if (original != mirrored) {
                sb.setCharAt(n, mirrored);
            }
        }
        return sb;
    }

    public void draw(Batch batch, float dx, float dy, float visibleGlyphs) {
        if (visibleGlyphs == 0 || glyphLayout.runs.size == 0) {
            return; // Nothing to draw
        }

        applyScale();
        {
            if (visibleGlyphs < 0f || visibleGlyphs >= glyphCount) {
                // Fully visible
                font.draw(batch, glyphLayout, getX() + dx, getY() + dy);
            } else {
                // Partially visible
                int visible = (int)visibleGlyphs;

                GlyphRun run = glyphLayout.runs.first();
                Array<Glyph> glyphs = run.glyphs;
                FloatArray xAdvances = run.xAdvances;

                Object[] oldGlyphs = glyphs.items;
                float[] oldXAdvances = xAdvances.items;
                int oldSize = glyphs.size;
                if (isRightToLeft()) {
                    int invisible = oldSize - visible;
                    for (int n = 0; n < invisible; n++) {
                        dx += xAdvances.get(n);
                    }

                    setGlyphs(glyphs, Arrays.copyOfRange(oldGlyphs, invisible, oldSize));
                    xAdvances.items = Arrays.copyOfRange(oldXAdvances, invisible, oldSize);
                }
                glyphs.size = visible;

                font.draw(batch, glyphLayout, getX() + dx, getY() + dy);

                if (isRightToLeft()) {
                    setGlyphs(glyphs, oldGlyphs);
                    xAdvances.items = oldXAdvances;
                }
                glyphs.size = oldSize;
            }
        }
        resetScale();
    }

    /** Ugly code needed because Array uses an unchecked cast from Object[] to T[] */
    private static void setGlyphs(Array<Glyph> array, Object[] newGlyphs) {
        array.clear();
        for (Object obj : newGlyphs) {
            array.add((Glyph)obj);
        }
    }

    private void applyScale() {
        font.getData().setScale(scaleXY);
    }

    private void resetScale() {
        font.getData().setScale(originalScaleX, originalScaleY);
    }

    @Override
    public int getGlyphCount() {
        return glyphCount;
    }

    @Override
    public TextStyle getGlyphStyle(int glyphIndex) {
        return style;
    }

    private static int getGlyphCount(GlyphLayout layout) {
        int count = 0;
        for (GlyphRun run : layout.runs) {
            count += run.glyphs.size;
        }
        return count;
    }

    @Override
    public float getAscent() {
        return capHeight;
    }

}
