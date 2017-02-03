package nl.weeaboo.vn.gdx.graphics;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.base.Preconditions;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;

/**
 * Variant of {@link FileTextureData} that generates texture data with premultiplied alpha.
 */
class PremultFileTextureData implements TextureData {

    private final FileHandle file;
    private final boolean mipmap;

    // Only available between prepare() and consumePixmap()
    private Pixmap pixmap;

    // Store some data we still need after consumePixmap() is called by the framework
    private boolean initialized;
    private Format format;
    private int width;
    private int height;

    /**
     * @param format (optional) If not {@code null}, convert the pixmap to this format before uploading it as
     *        texture data.
     */
    public PremultFileTextureData(FileHandle file, Format format, boolean mipmap) {
        this.file = Checks.checkNotNull(file);
        this.format = format; // May be null
        this.mipmap = mipmap;
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
            pixmap = ensurePot(pixmap);

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
            pixmap = new Pixmap(file);
        } catch (GdxRuntimeException e) {
            throw new IOException("Error loading texture: " + file);
        }
        PremultUtil.premultiplyAlpha(pixmap);
        return pixmap;
    }

    @Override
    public void consumeCustomData(int target) {
        throw new UnsupportedOperationException("JNG texture data is of type PIXMAP");
    }

    @Override
    public Pixmap consumePixmap() {
        Pixmap result = pixmap;
        pixmap = null;
        return result;
    }

    @Override
    public boolean disposePixmap() {
        return true;
    }

    @Override
    public Format getFormat() {
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

    protected Pixmap ensurePot(Pixmap pixmap) {
        if (Gdx.gl20 == null && FileTextureData.copyToPOT) {
            int pixmapWidth = pixmap.getWidth();
            int pixmapHeight = pixmap.getHeight();
            int potWidth = MathUtils.nextPowerOfTwo(pixmapWidth);
            int potHeight = MathUtils.nextPowerOfTwo(pixmapHeight);
            if (pixmapWidth != potWidth || pixmapHeight != potHeight) {
                Pixmap copy = PixmapUtil.resizedCopy(pixmap, Dim.of(potWidth, potHeight), Filter.BiLinear);
                pixmap.dispose();
                return copy;
            }
        }
        return pixmap;
    }

}
