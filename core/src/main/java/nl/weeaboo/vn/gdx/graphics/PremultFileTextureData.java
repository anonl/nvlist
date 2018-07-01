package nl.weeaboo.vn.gdx.graphics;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.base.Preconditions;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.gdx.graphics.jng.JngReader;
import nl.weeaboo.vn.gdx.graphics.jng.JngReaderOpts;
import nl.weeaboo.vn.impl.core.DurationLogger;

/**
 * Variant of {@link FileTextureData} that generates texture data with premultiplied alpha.
 */
class PremultFileTextureData implements TextureData {

    private static final Logger LOG = LoggerFactory.getLogger(PremultFileTextureData.class);

    private final FileHandle file;
    private final boolean mipmap;
    private final boolean needsPremultiplyAlpha;

    // Only available between prepare() and consumePixmap()
    private @Nullable Pixmap pixmap;

    // Store some data we still need after consumePixmap() is called by the framework
    private boolean initialized;
    private @Nullable Format format;
    private int width;
    private int height;

    /**
     * @param format (optional) If not {@code null}, convert the pixmap to this format before uploading it as
     *        texture data.
     */
    public PremultFileTextureData(FileHandle file, @Nullable Format format, boolean mipmap,
            boolean needsPremultiplyAlpha) {

        this.file = Checks.checkNotNull(file);
        this.format = format; // May be null
        this.mipmap = mipmap;
        this.needsPremultiplyAlpha = needsPremultiplyAlpha;
    }

    @Override
    public TextureDataType getType() {
        return TextureDataType.Pixmap;
    }

    @Override
    public boolean isPrepared() {
        return pixmap != null;
    }

    @Override
    public void prepare() {
        Preconditions.checkState(!isPrepared(), "Already prepared");

        initialized = false;
        try {
            pixmap = loadPixmap(file);

            if (format == null) {
                format = pixmap.getFormat();
            }
            width = pixmap.getWidth();
            height = pixmap.getHeight();
            initialized = true;
        } catch (IOException e) {
            // Throw runtime exception to be compatible with FileTextureData
            throw new GdxRuntimeException("Couldn't load file: " + file, e);
        }
    }

    protected Pixmap loadPixmap(FileHandle file) throws IOException {
        Pixmap pixmap;
        try {

            int targetFormat = 0;
            if (format != null) {
                targetFormat = Format.toGdx2DPixmapFormat(format);
            }

            DurationLogger fileReadStopwatch = DurationLogger.createStarted(LOG);
            byte[] fileBytes = file.readBytes();
            fileReadStopwatch.logDuration("PremultTextureData.readFile: {}", file);

            DurationLogger decodeStopwatch = DurationLogger.createStarted(LOG);
            if (JngReader.isJng(fileBytes, 0, fileBytes.length)) {
                // .jng files
                JngReaderOpts opts = new JngReaderOpts();
                opts.resultFormat = format;
                pixmap = JngReader.read(new ByteArrayInputStream(fileBytes), opts);
            } else {
                // .jpg, .png files
                pixmap = new Pixmap(new Gdx2DPixmap(fileBytes, 0, fileBytes.length,
                        targetFormat));
            }
            decodeStopwatch.logDuration("PremultTextureData.decodePixmap: {}", file);
        } catch (GdxRuntimeException e) {
            throw new IOException("Error loading texture: " + file);
        }

        if (needsPremultiplyAlpha) {
            DurationLogger premultiplyStopwatch = DurationLogger.createStarted(LOG);
            PremultUtil.premultiplyAlpha(pixmap);
            premultiplyStopwatch.logDuration("PremultTextureData.premultiplyAlpha: {}", file);
        }
        return pixmap;
    }

    @Override
    public void consumeCustomData(int target) {
        throw new UnsupportedOperationException("JNG texture data is of type PIXMAP");
    }

    @Override
    public Pixmap consumePixmap() {
        final Pixmap result = pixmap;
        if (result == null) {
            throw new IllegalStateException("Pixmap is null; did you forget to call prepare()?");
        }

        pixmap = null;
        return result;
    }

    @Override
    public boolean disposePixmap() {
        return true;
    }

    @Override
    public @Nullable Format getFormat() {
        checkInitialized();
        return format;
    }

    @Override
    public int getWidth() {
        checkInitialized();
        return width;
    }

    @Override
    public int getHeight() {
        checkInitialized();
        return height;
    }

    private void checkInitialized() {
        Preconditions.checkState(initialized, "Must call prepare() first");
    }

    @Override
    public boolean useMipMaps() {
        return mipmap;
    }

    @Override
    public boolean isManaged() {
        return true;
    }

}
