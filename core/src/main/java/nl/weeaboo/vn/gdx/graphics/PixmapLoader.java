package nl.weeaboo.vn.gdx.graphics;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.gdx.graphics.jng.JngReader;

public final class PixmapLoader {

    private PixmapLoader() {
    }

    /**
     * Reads a pixmap from a file in the file system.
     *
     * @throws IOException If the file can't be read.
     */
    public static Pixmap load(IFileSystem fileSystem, FilePath path) throws IOException {
        byte[] encodedData = FileSystemUtil.readBytes(fileSystem, path);
        return load(encodedData, 0, encodedData.length);
    }

    /**
     * Reads a pixmap from encoded data.
     *
     * @throws IOException If the file can't be read.
     */
    public static Pixmap load(byte[] bytes, int offset, int length) throws IOException {
        if (JngReader.isJng(bytes, offset, length)) {
            return JngReader.read(new ByteArrayInputStream(bytes, offset, length));
        }
        return new Pixmap(bytes, offset, length);
    }

}
