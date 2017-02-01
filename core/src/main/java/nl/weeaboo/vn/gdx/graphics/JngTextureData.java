package nl.weeaboo.vn.gdx.graphics;

import java.io.IOException;
import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.StreamUtils;

/**
 * Texture data based on a .jng file source.
 */
public final class JngTextureData extends PremultFileTextureData {

    public JngTextureData(FileHandle file, Format format, boolean mipmap) {
        super(file, format, mipmap);
    }

    @Override
    protected Pixmap loadPixmap(FileHandle file) throws IOException {
        Pixmap pixmap;

        InputStream in = file.read();
        try {
            pixmap = JngReader.read(in);
        } finally {
            StreamUtils.closeQuietly(in);
        }

        PremultUtil.premultiplyAlpha(pixmap);
        return pixmap;
    }

}
