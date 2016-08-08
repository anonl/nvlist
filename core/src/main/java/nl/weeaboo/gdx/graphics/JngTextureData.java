package nl.weeaboo.gdx.graphics;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.google.common.base.Preconditions;

/**
 * Texture data based on a .jng file source.
 */
public class JngTextureData implements TextureData {

    private final FileHandle file;
    private final boolean mipmap;

    // Only available between prepare() and consumePixmap()
    private Pixmap pixmap;

    // Store some data we still need after consumePixmap() is called by the framework
    private boolean initialized;
    private Format format;
    private int width, height;

    public JngTextureData(FileHandle file, boolean mipmap) {
        this.file = Preconditions.checkNotNull(file);
        this.mipmap = Preconditions.checkNotNull(mipmap);
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
        InputStream in = file.read();
        try {
            pixmap = JngReader.read(in);

            format = pixmap.getFormat();
            width = pixmap.getWidth();
            height = pixmap.getHeight();
            initialized = true;
        } catch (IOException e) {
            // Throw runtime exception to be compatible with FileTextureData
            throw new GdxRuntimeException("Couldn't load file: " + file, e);
        } finally {
            StreamUtils.closeQuietly(in);
        }
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

}
