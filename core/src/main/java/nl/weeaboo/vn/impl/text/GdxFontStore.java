package nl.weeaboo.vn.impl.text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.io.Filenames;
import nl.weeaboo.styledtext.EFontStyle;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.gdx.GdxFont;
import nl.weeaboo.styledtext.gdx.GdxFontGenerator;
import nl.weeaboo.styledtext.gdx.GdxFontRegistry;
import nl.weeaboo.styledtext.gdx.YDir;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.gdx.res.ResourceStore;
import nl.weeaboo.vn.gdx.res.ResourceStoreCache;
import nl.weeaboo.vn.gdx.res.ResourceStoreCacheConfig;
import nl.weeaboo.vn.impl.core.LruSet;
import nl.weeaboo.vn.text.ILoadingFontStore;

/**
 * Low-level font resource loader.
 *
 * @see ILoadingFontStore
 */
public final class GdxFontStore extends ResourceStore {

    private static final Logger LOG = LoggerFactory.getLogger(GdxFontStore.class);
    private static final ImmutableSet<String> FONT_FILE_EXTS = ImmutableSet.of("ttf");

    private final GdxFileSystem resourceFileSystem;
    private final GdxFontRegistry backing;
    private final FontCache cache;
    private final LruSet<FilePath> missingFonts = new LruSet<>(16);

    public GdxFontStore(GdxFileSystem resourceFileSystem) {
        super(LOG);

        this.resourceFileSystem = Checks.checkNotNull(resourceFileSystem);

        backing = new GdxFontRegistry();
        cache = new FontCache(new ResourceStoreCacheConfig<>());
    }

    private void loadInitialFonts() {
        try {
            loadFont(Gdx.files.classpath("builtin/font/default.ttf"),
                    new TextStyle(TextUtil.DEFAULT_FONT_NAME, 16));
        } catch (IOException e) {
            LOG.warn("Error loading built-in font", e);
        }

        for (FilePath fontPath : resourceFileSystem.getFiles(FileCollectOptions.files(FilePath.of("font")))) {
            String ext = fontPath.getExt();
            if (!FONT_FILE_EXTS.contains(ext)) {
                LOG.debug("Skipping over non-font file in fonts folder: {}", fontPath);
                continue;
            }

            try {
                MutableTextStyle mts = new MutableTextStyle();
                mts.setFontName(Filenames.stripExtension(fontPath.getName()));
                loadFont(fontPath, mts.immutableCopy());
            } catch (IOException e) {
                LOG.warn("Error loading in font: {}", fontPath, e);
            }
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }

    private GdxFont loadFont(FilePath absoluteFontPath, TextStyle ts) throws IOException {
        for (FilePath variantPath : getStyleSpecificPaths(absoluteFontPath, ts.getFontStyle(EFontStyle.PLAIN))) {
            FileHandle fileHandle = resourceFileSystem.resolve(variantPath.toString());
            if (fileHandle.exists()) {
                LOG.debug("Selected font variant to load: {}", variantPath);

                return loadFont(fileHandle, ts);
            }
        }

        throw new FileNotFoundException(absoluteFontPath.toString());
    }

    private GdxFont loadFont(FileHandle file, TextStyle ts) throws IOException {
        GdxFontGenerator fontGenerator = new GdxFontGenerator();
        fontGenerator.setYDir(YDir.DOWN);

        GdxFont font = fontGenerator.load(file, ts);
        backing.addFont(font);
        return font;
    }

    private List<FilePath> getStyleSpecificPaths(FilePath path, EFontStyle fontStyle) {
        FilePath parent = path.getParent();
        if (parent == null) {
            parent = FilePath.empty();
        }
        String baseName = Filenames.stripExtension(path.getName());
        String ext = Filenames.getExtension(path.getName());

        FilePath bold = parent.resolve(baseName + "-bold." + ext);
        FilePath boldOld = parent.resolve(baseName + ".bold." + ext);
        FilePath italic = parent.resolve(baseName + "-italic." + ext);
        FilePath italicOld = parent.resolve(baseName + ".italic." + ext);
        FilePath boldItalic = parent.resolve(baseName + "-bolditalic." + ext);
        FilePath boldItalicOld = parent.resolve(baseName + ".bolditalic." + ext);

        switch (fontStyle) {
        case BOLD:
            return ImmutableList.of(boldOld, bold, path);
        case ITALIC:
            return ImmutableList.of(italicOld, italic, path);
        case BOLD_ITALIC:
            return ImmutableList.of(boldItalicOld, boldItalic, boldOld, bold, italicOld, italic, path);
        default:
            return ImmutableList.of(path);
        }
    }

    /**
     * Returns a font object matching the given font file and text style. If loading fails, a different font
     * may be returned instead.
     */
    public IFontMetrics getFontMetrics(FilePath absoluteFontPath, TextStyle styleArg) {
        if (backing.getFonts().isEmpty()) {
            loadInitialFonts();
        }

        // Workaround for a bug in gdx-styledtext -- a null font name causes problems
        TextStyle style = styleArg;
        if (style.getFontName() == null) {
            MutableTextStyle mts = style.mutableCopy();
            mts.setFontName(TextUtil.DEFAULT_FONT_NAME);
            style = mts.immutableCopy();
        }

        // Only attempt to load each font once
        if (!absoluteFontPath.equals(FilePath.empty()) && !missingFonts.contains(absoluteFontPath)) {
            // Load font (if needed)
            try {
                cache.get(new CacheKey(absoluteFontPath, style));
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

    private final class FontCache extends ResourceStoreCache<CacheKey, GdxFont> {

        public FontCache(ResourceStoreCacheConfig<GdxFont> config) {
            super(config);
        }

        @Override
        public GdxFont doLoad(CacheKey key) throws IOException {
            try {
                return loadFont(key.absolutePath, key.style);
            } catch (RuntimeException re) {
                loadError(key.absolutePath, re);
                throw new IOException("Error loading file: " + key.absolutePath, re);
            }
        }

        @Override
        protected void doUnload(CacheKey key, @Nullable GdxFont value) {
            /*
             * We can't dispose the font object here since it may be still be referenced directly by
             * GdxFont/GdxTextElement/...
             */
            if (value != null) {
                backing.removeFont(value);
            }
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

        @Override
        public String toString() {
            return StringUtil.formatRoot("CacheKey[path=%s, style=%s]", absolutePath, style);
        }

    }

}
