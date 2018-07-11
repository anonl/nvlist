package nl.weeaboo.vn.impl.text;

import java.io.IOException;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.gdx.GdxFontGenerator;
import nl.weeaboo.styledtext.gdx.GdxFontInfo;
import nl.weeaboo.styledtext.gdx.GdxFontStore;
import nl.weeaboo.styledtext.gdx.YDir;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.text.ILoadingFontStore;

public final class LoadingFontStore implements ILoadingFontStore {

    private static final Logger LOG = LoggerFactory.getLogger(LoadingFontStore.class);

    private final GdxFontStore backing;

    private boolean destroyed;

    public LoadingFontStore(GdxFileSystem resourceFileSystem) {
        backing = new GdxFontStore();

        try {
            String fontFamily = "RobotoSlab";
            int[] sizes = { 16, 32 };
            for (EFontStyle style : EnumSet.of(EFontStyle.PLAIN, EFontStyle.BOLD, EFontStyle.ITALIC)) {
                String name = fontFamily;
                if (style.isBold()) {
                    name += "Bold";
                }
                if (style.isItalic()) {
                    name += "Oblique";
                }

                MutableTextStyle ts = new MutableTextStyle();
                ts.setFontName(name);
                ts.setFontStyle(style);

                FileHandle fileHandle = resourceFileSystem.resolve("font/" + name + ".ttf");

                GdxFontGenerator fontGenerator = new GdxFontGenerator();
                fontGenerator.setYDir(YDir.DOWN);
                GdxFontInfo[] fonts = fontGenerator.load(fileHandle, ts.immutableCopy(), sizes);
                for (int n = 0; n < fonts.length; n++) {
                    backing.registerFont(fonts[n]);
                }
            }
        } catch (IOException ioe) {
            LOG.warn("Unable to load font(s)", ioe);
        }
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        return backing.getFontMetrics(style);
    }

}
