package nl.weeaboo.vn.save.impl;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IWritableFileSystem;

public final class SaveTestUtil {

    private SaveTestUtil() {
    }

    public static void writeDummySave(SaveModule saveModule, int saveSlot) throws IOException {
        IWritableFileSystem fs = saveModule.getFileSystem();
        FilePath savePath = saveModule.getSavePath(saveSlot);

        ZipOutputStream zout = new ZipOutputStream(fs.openOutputStream(savePath, false));
        try {
            SaveFileHeader header = new SaveFileHeader(System.currentTimeMillis());
            SaveFileIO.writeJson(zout, SaveFileConstants.HEADER_PATH, SaveFileHeaderJson.encode(header));

            // No save data
        } finally {
            zout.close();
        }
    }
}
