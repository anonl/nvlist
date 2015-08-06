package nl.weeaboo.gdx.styledtext;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import nl.weeaboo.styledtext.layout.ILayoutElement;
import nl.weeaboo.styledtext.layout.ITextLayout;

public final class GdxFontUtil {

    private GdxFontUtil() {
    }

    public static void draw(Batch batch, ITextLayout layout, float dx, float dy, float visibleGlyphs) {
        if (visibleGlyphs < 0) {
            visibleGlyphs = 1e9f;
        }

        for (ILayoutElement elem : layout.getElements()) {
            if (!(elem instanceof GdxTextElement)) {
                continue;
            }

            GdxTextElement textElem = (GdxTextElement)elem;
            textElem.draw(batch, dx, dy, visibleGlyphs);

            // Decrease visible glyphs
            visibleGlyphs -= textElem.getGlyphCount();
            if (visibleGlyphs <= 0) {
                break;
            }
        }
    }

    public static BitmapFont[] load(String fontPath, int... sizes) throws IOException {
        FileHandle fontFile = Gdx.files.internal(fontPath);
        if (!fontFile.exists()) {
            throw new FileNotFoundException(fontPath);
        }

        BitmapFont[] result = new BitmapFont[sizes.length];

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        try {
            for (int n = 0; n < sizes.length; n++) {
                FreeTypeFontParameter parameter = new FreeTypeFontParameter();
                parameter.size = sizes[n];

                BitmapFont bmFont = generator.generateFont(parameter);
                bmFont.setUseIntegerPositions(true);
                result[n] = bmFont;
            }
        } finally {
            generator.dispose();
        }

        return result;
    }
}
