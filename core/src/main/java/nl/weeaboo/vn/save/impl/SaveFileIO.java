package nl.weeaboo.vn.save.impl;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.ZipFileArchive;
import nl.weeaboo.io.RandomAccessUtil;
import nl.weeaboo.io.ZipUtil;
import nl.weeaboo.io.ZipUtil.Compression;

final class SaveFileIO {

    private SaveFileIO() {
    }

    public static IFileSystem openArchive(IFileSystem fs, String path) throws IOException {
        byte[] bytes = FileSystemUtil.readBytes(fs, path);

        ZipFileArchive zipArchive = new ZipFileArchive();
        zipArchive.open(RandomAccessUtil.wrap(bytes, 0, bytes.length));
        return zipArchive;
    }

    public static String read(IFileSystem fs, String path) throws IOException {
        return FileSystemUtil.readString(fs, path);
    }

    public static byte[] readBytes(IFileSystem fs, String path) throws IOException {
        return FileSystemUtil.readBytes(fs, path);
    }

    public static <T> T readJson(IFileSystem fs, String path, Class<T> type) throws IOException {
        return JsonUtil.fromJson(type, read(fs, path));
    }

    public static <T> void writeJson(ZipOutputStream zout, String path, T object) throws IOException {
        String fileData = JsonUtil.toJson(object);
        write(zout, path, fileData);
    }

    public static void write(ZipOutputStream zout, String path, String fileData) throws IOException {
        writeBytes(zout, path, StringUtil.toUTF8(fileData));
    }

    public static void writeBytes(ZipOutputStream zout, String path, byte[] fileData) throws IOException {
        ZipUtil.writeFileEntry(zout, path, fileData, 0, fileData.length, Compression.DEFLATE);
    }

}
