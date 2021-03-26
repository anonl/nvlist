package nl.weeaboo.vn.impl.save;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.vn.impl.image.PixelTextureData;
import nl.weeaboo.vn.impl.image.TestImageUtil;
import nl.weeaboo.vn.save.ThumbnailInfo;

/**
 * Various utility functions for unit testing the save system.
 */
public final class SaveTestUtil {

    private static final Dim THUMBNAIL_SIZE = Dim.of(10, 10);

    private SaveTestUtil() {
    }

    private static byte[] getThumbnailBytes() throws IOException {
        PixelTextureData pixels = TestImageUtil.newTestTextureData(THUMBNAIL_SIZE.w, THUMBNAIL_SIZE.h);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        TestImageUtil.writePng(pixels.borrowPixels(), bout);
        return bout.toByteArray();
    }

    /**
     * Writes an empty save file to the specified save slot.
     * @throws IOException If an I/O error occurs while writing the save file.
     */
    public static void writeDummySave(SaveModule saveModule, int saveSlot) throws IOException {
        IWritableFileSystem fs = saveModule.getFileSystem();
        FilePath savePath = saveModule.getSavePath(saveSlot);

        ThumbnailInfo thumbnailInfo = new ThumbnailInfo(THUMBNAIL_SIZE);

        ZipOutputStream zout = new ZipOutputStream(fs.openOutputStream(savePath, false));
        try {
            SaveFileHeader header = new SaveFileHeader(System.currentTimeMillis());
            header.setThumbnail(thumbnailInfo);
            SaveFileIO.writeJson(zout, SaveFileConstants.HEADER_PATH, SaveFileHeaderJson.encode(header));
            SaveFileIO.writeBytes(zout, thumbnailInfo.getPath(), getThumbnailBytes());

            // No save data
        } finally {
            zout.close();
        }
    }

    /**
     * Writes an invalid save file to the specified save slot.
     * @throws IOException If an I/O error occurs while writing the save file.
     */
    public static void writeBrokenSave(SaveModule saveModule, int saveSlot) throws IOException {
        IWritableFileSystem fs = saveModule.getFileSystem();
        FilePath savePath = saveModule.getSavePath(saveSlot);

        try (ZipOutputStream zout = new ZipOutputStream(fs.openOutputStream(savePath, false))) {
            // Doesn't even contain a header
        }
    }

}
