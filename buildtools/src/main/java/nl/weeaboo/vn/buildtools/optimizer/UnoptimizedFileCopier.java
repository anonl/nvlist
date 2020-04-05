package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;

final class UnoptimizedFileCopier {

    private static final Logger LOG = LoggerFactory.getLogger(UnoptimizedFileCopier.class);

    public void copyOtherResources(IFileSystem resFileSystem, IOptimizerFileSet fileSet, File dstFolder) {
        for (FilePath path : getAllFilesAndFolders(resFileSystem)) {
            if (fileSet.isOptimized(path)) {
                continue;
            }

            // File wasn't processed by an optimizer, so just copy it.
            File dstFile = new File(dstFolder, path.toString());
            if (resFileSystem.isFolder(path)) {
                if (!dstFile.isDirectory() && !dstFile.mkdirs()) {
                    LOG.warn("Unable to create folder: {}", dstFile);
                }
            } else {
                try (InputStream in = resFileSystem.openInputStream(path)) {
                    if (dstFile.exists()) {
                        LOG.error("Error copying file (destination already exists): {}", dstFile);
                    }

                    Files.createParentDirs(dstFile);
                    try (OutputStream out = new FileOutputStream(dstFile)) {
                        ByteStreams.copy(in, out);
                    }
                } catch (IOException ioe) {
                    LOG.warn("Error copying file: {} -> {}", path, dstFile, ioe);
                }
            }
        }
    }

    private Iterable<FilePath> getAllFilesAndFolders(IFileSystem resFileSystem) {
        FileCollectOptions opts = new FileCollectOptions();
        opts.collectFolders = true;
        return resFileSystem.getFiles(opts);
    }

}
