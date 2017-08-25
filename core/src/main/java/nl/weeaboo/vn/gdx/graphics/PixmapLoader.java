package nl.weeaboo.vn.gdx.graphics;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;

public final class PixmapLoader {

    private PixmapLoader() {
    }

    public static Pixmap load(IFileSystem fileSystem, FilePath path) throws IOException {
        byte[] encodedData = FileSystemUtil.readBytes(fileSystem, path);

        if (path.getExt().equals("jng")) {
            return JngReader.read(new ByteArrayInputStream(encodedData));
        }

        return new Pixmap(encodedData, 0, encodedData.length);
    }

}
