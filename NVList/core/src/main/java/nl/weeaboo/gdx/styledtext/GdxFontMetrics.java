package nl.weeaboo.gdx.styledtext;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.styledtext.layout.ILayoutElement;
import nl.weeaboo.styledtext.layout.LayoutParameters;
import nl.weeaboo.styledtext.layout.SpacingElement;

public class GdxFontMetrics implements IFontMetrics {

    private final BitmapFont font;
    private final float scale;

    public GdxFontMetrics(BitmapFont font, float scale) {
        this.font = font;
        this.scale = scale;
    }

    @Override
    public float getSpaceWidth() {
        return font.getData().spaceWidth * scale;
    }

    @Override
    public float getLineHeight() {
        return font.getData().lineHeight * scale;
    }

    @Override
    public ILayoutElement layoutText(CharSequence str, TextStyle style, LayoutParameters params) {
        return new GdxTextElement(str, style, font);
    }

    @Override
    public ILayoutElement layoutSpacing(CharSequence text, TextStyle style, LayoutParameters params) {
        int numSpaces = 0;
        for (int n = 0; n < text.length(); n++) {
            char c0 = text.charAt(n);
            int codepoint = c0;
            if (Character.isHighSurrogate(c0)) {
                n++;
                codepoint = Character.toCodePoint(c0, text.charAt(n));
            }

            if (codepoint == '\t') {
                numSpaces += 4;
            } else {
                numSpaces += 1;
            }
        }

        return new SpacingElement(numSpaces * getSpaceWidth(), getLineHeight());
    }

    public static float getScale(TextStyle style, BitmapFont font) {
        return style.getFontSize() / getSize(font);
    }

    private static float getSize(BitmapFont font) {
        return font.getCapHeight() - font.getDescent();
    }

}
