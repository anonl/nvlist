package nl.weeaboo.vn.impl.save;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.vn.impl.save.SaveFileConstants;
import nl.weeaboo.vn.impl.save.SaveFileHeader;
import nl.weeaboo.vn.impl.save.SaveFileHeaderJson;
import nl.weeaboo.vn.impl.save.SaveFileIO;
import nl.weeaboo.vn.impl.save.SaveModule;

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
