package nl.weeaboo.vn.impl.save;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IWritableFileSystem;

public final class SaveTestUtil {

    private SaveTestUtil() {
    }

    /**
     * Writes an empty save file to the specified save slot.
     * @throws IOException If an I/O error occurs while writing the save file.
     */
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
