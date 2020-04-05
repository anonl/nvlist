package nl.weeaboo.vn.gdx.res;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.base.MoreObjects;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;

/**
 * File system which resolves files using {@link Files#internal}.
 */
public final class InternalGdxFileSystem extends GdxFileSystem {

    private static final Logger LOG = LoggerFactory.getLogger(InternalGdxFileSystem.class);

    private final String prefix;

    public InternalGdxFileSystem(String prefix) {
        super(true);

        this.prefix = Checks.checkNotNull(prefix);
    }

    @Override
    public FileHandle resolve(String fileName) {
        FilePath fullPath = FilePath.of(prefix + fileName);
        FileHandle fileHandle = Gdx.files.internal(fullPath.toString());

        File backingFile = fileHandle.file();
        if (!backingFile.exists()) {
            return new InvalidFileHandle(FilePath.of(fileName), FileType.Internal);
        }

        // Make file system always act as case-sensitive; pretend file doesn't exist if case-sensitivity matters
        try {
            FilePath canonicalPath = FilePath.of(backingFile.getCanonicalPath());
            if (!canonicalPath.endsWith(fullPath)) {
                LOG.warn("Attempted to access file using the wrong name: given={}, actual={}",
                        fullPath, canonicalPath);
                fileHandle = new InvalidFileHandle(FilePath.of(fileName), FileType.Internal);
            }
        } catch (IOException ioe) {
            LOG.warn("Unable to resolve canonical path for: {}", backingFile, ioe);
        }

        return fileHandle;
    }

    private final class InvalidFileHandle extends NonFileGdxFileHandle {

        private final FilePath filePath;

        public InvalidFileHandle(FilePath filePath, FileType type) {
            super(filePath.toString(), type);

            this.filePath = filePath;
        }

        @Override
        public InputStream read() {
            throw invalidFileException();
        }

        @Override
        public @Nullable FileHandle parent() {
            return resolve(MoreObjects.firstNonNull(filePath.getParent(), FilePath.empty()));
        }

        @Override
        public @Nullable FileHandle child(String name) {
            return resolve(filePath.resolve(name));
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        protected Iterable<FileHandle> listChildren() {
            return Collections.emptyList();
        }

        @Override
        public long length() {
            return 0;
        }

        @Override
        public boolean exists() {
            return false;
        }

        private GdxRuntimeException invalidFileException() {
            throw gdxException("File doesn't exist: " + path());
        }
    }
}
