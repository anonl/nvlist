package nl.weeaboo.vn.impl.text;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.gdx.GdxFontGenerator;
import nl.weeaboo.styledtext.gdx.GdxFontInfo;
import nl.weeaboo.styledtext.gdx.YDir;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.vn.gdx.res.AbstractResourceStore;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.gdx.res.ResourceStoreCache;
import nl.weeaboo.vn.gdx.res.ResourceStoreCacheConfig;
import nl.weeaboo.vn.impl.core.LruSet;

public final class GdxFontStore extends AbstractResourceStore {

    private static final Logger LOG = LoggerFactory.getLogger(GdxFontStore.class);

    private final GdxFileSystem resourceFileSystem;
    private final nl.weeaboo.styledtext.gdx.GdxFontStore backing;
    private final Cache cache;
    private final LruSet<FilePath> missingFonts = new LruSet<>(16);

    public GdxFontStore(GdxFileSystem resourceFileSystem) {
        super(LOG);

        this.resourceFileSystem = Checks.checkNotNull(resourceFileSystem);

        backing = new nl.weeaboo.styledtext.gdx.GdxFontStore();
        cache = new Cache(new ResourceStoreCacheConfig<>());
    }

    private void loadBuiltInFont() {
        try {
            loadFont(Gdx.files.classpath("builtin/font/default.ttf"),
                    new TextStyle(FontResourceLoader.DEFAULT_FONT_NAME, 16));
        } catch (IOException e) {
            LOG.warn("Error loading built-in font", e);
        }
    }

    @Override
    public void destroy() {
        if (!isDestroyed()) {
            super.destroy();

            backing.dispose();
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }

    private GdxFontInfo loadFont(FilePath absoluteFontPath, TextStyle ts) throws IOException {
        FileHandle fileHandle = resourceFileSystem.resolve(absoluteFontPath.toString());
        return loadFont(fileHandle, ts);
    }

    private GdxFontInfo loadFont(FileHandle file, TextStyle ts) throws IOException {
        GdxFontGenerator fontGenerator = new GdxFontGenerator();
        fontGenerator.setYDir(YDir.DOWN);

        GdxFontInfo font = fontGenerator.load(file, ts);
        backing.registerFont(font);
        return font;
    }

    /**
     * Returns a font object matching the given font file and text style. If loading fails, a different font
     * may be returned instead.
     */
    public IFontMetrics getFontMetrics(FilePath absoluteFontPath, TextStyle styleArg) {
        // Add the default font if we didn't do that yet
        if (backing.getFonts().isEmpty()) {
            loadBuiltInFont();
        }

        // Workaround for a bug in gdx-styledtext -- a null font name causes problems
        MutableTextStyle mts = styleArg.mutableCopy();
        if (styleArg.getFontName() == null) {
            mts.setFontName(FontResourceLoader.DEFAULT_FONT_NAME);
        }
        TextStyle style = mts.immutableCopy();

        // Only attempt to load each font once
        if (!missingFonts.contains(absoluteFontPath)) {
            // Load font (if needed)
            try {
                cache.getEntry(new CacheKey(absoluteFontPath, style));
            } catch (ExecutionException e) {
                loadError(absoluteFontPath, e.getCause());
            }
        }

        // Return font metrics
        return backing.getFontMetrics(style);
    }

    @Override
    protected void loadError(FilePath path, Throwable cause) {
        if (missingFonts.add(path)) {
            super.loadError(path, cause);
        }
    }

    private final class Cache extends ResourceStoreCache<CacheKey, GdxFontInfo> {

        public Cache(ResourceStoreCacheConfig<GdxFontInfo> config) {
            super(config);
        }

        @Override
        public GdxFontInfo doLoad(CacheKey key) throws IOException {
            try {
                return loadFont(key.absolutePath, key.style);
            } catch (RuntimeException re) {
                loadError(key.absolutePath, re);
                throw re;
            }
        }

        @Override
        protected void doUnload(CacheKey key, @Nullable GdxFontInfo value) {
            backing.disposeFont(value);
        }

    }

    private static final class CacheKey {

        final FilePath absolutePath;
        final TextStyle style;

        public CacheKey(FilePath absolutePath, TextStyle style) {
            this.absolutePath = Checks.checkNotNull(absolutePath);
            this.style = Checks.checkNotNull(style);
        }

        @Override
        public int hashCode() {
            return style.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CacheKey)) {
                return false;
            }

            CacheKey other = (CacheKey)obj;
            return absolutePath.equals(other.absolutePath) && style.equals(other.style);
        }

    }

}
