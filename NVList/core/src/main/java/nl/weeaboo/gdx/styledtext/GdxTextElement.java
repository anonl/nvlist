package nl.weeaboo.gdx.styledtext;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.TextElement;

final class GdxTextElement extends TextElement {

    private final TextStyle style;
    private final BitmapFont font;
    private final float originalScaleX, originalScaleY;
    private final float scaleXY;

    private GlyphLayout glyphLayout;
    private int glyphCount;

    public GdxTextElement(CharSequence str, TextStyle style, BitmapFont font) {
        super(style.getAlign());

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
            glyphLayout = new GlyphLayout(font, str, color, 0, Align.left, false);
            if (glyphLayout.runs.size > 1) {
                throw new IllegalArgumentException(
                        "Arguments result in a layout with multiple glyph runs: " + glyphLayout.runs.size);
            }

            glyphCount = getGlyphCount(glyphLayout);
            setLayoutWidth(glyphLayout.width);
            setLayoutHeight(glyphLayout.height);
        }
        resetScale();
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
                Array<Glyph> glyphs = glyphLayout.runs.first().glyphs;
                int oldSize = glyphs.size;
                glyphs.size = (int)visibleGlyphs;
                font.draw(batch, glyphLayout, getX() + dx, getY() + dy);
                glyphs.size = oldSize;
            }
        }
        resetScale();
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

}
