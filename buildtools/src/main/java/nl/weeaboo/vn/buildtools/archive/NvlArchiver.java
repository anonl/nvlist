package nl.weeaboo.vn.buildtools.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipOutputStream;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.io.ZipUtil;
import nl.weeaboo.io.ZipUtil.Compression;

final class NvlArchiver implements IArchiver {

    @Override
    public void archiveFiles(IFileSystem fileSystem, File outputFile) throws IOException {
        FileCollectOptions collectOpts = new FileCollectOptions();
        collectOpts.collectFiles = true;
        collectOpts.collectFolders = true;
        collectOpts.recursive = true;

        final Compression compression = Compression.NONE;
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(outputFile))) {
            for (FilePath path : fileSystem.getFiles(collectOpts)) {
                if (path.isFolder()) {
                    ZipUtil.writeFolderEntry(zout, path.toString());
                } else {
                    try (InputStream entryIn = fileSystem.openInputStream(path)) {
                        long size = fileSystem.getFileSize(path);
                        ZipUtil.writeFileEntry(zout, path.toString(), entryIn, size, compression);
                    }
                }
            }
        }
    }

}
