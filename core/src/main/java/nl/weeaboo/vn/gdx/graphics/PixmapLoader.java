package nl.weeaboo.vn.gdx.graphics;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.gdx.graphics.jng.JngReader;
import nl.weeaboo.vn.gdx.graphics.jng.JngReaderOpts;
import nl.weeaboo.vn.gdx.res.NativeMemoryTracker;

/**
 * Loads {@link Pixmap} files.
 */
public final class PixmapLoader {

    private PixmapLoader() {
    }

    /**
     * Returns the set of file extensions (without the '.' prefix) supported by the load methods of this
     * class.
     */
    public static ImmutableSet<String> getSupportedImageExts() {
        return ImmutableSet.of(
                "png", "jpg", "bmp", // Supported by Pixmap
                "jng"); // Supported by JngReader
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
            return JngReader.read(new ByteArrayInputStream(bytes, offset, length),
                    new JngReaderOpts());
        }

        Pixmap pixmap = new Pixmap(bytes, offset, length);
        NativeMemoryTracker.get().register(pixmap);
        return pixmap;
    }

}
