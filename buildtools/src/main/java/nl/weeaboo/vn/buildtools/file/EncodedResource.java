package nl.weeaboo.vn.buildtools.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import com.google.common.io.ByteStreams;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;

public final class EncodedResource {

    private EncodedResource() {
    }

    /**
     * Note: the array is *not* copied.
     */
    public static IEncodedResource fromBytes(byte[] byteArray) {
        return new InMemoryResource(byteArray);
    }

    /**
     * Returns an {@link IEncodedResource} that reads its data from a file in the given filesystem.
     */
    public static IEncodedResource fromFileSystem(IFileSystem fileSystem, FilePath path) {
        return new FileSystemResource(fileSystem, path);
    }

    /**
     * Returns an {@link IEncodedResource} wrapping a temp file.
     */
    public static IEncodedResource fromTempFile(File file) {
        return new TempFileResource(file);
    }

    private static final class FileSystemResource implements IEncodedResource {

        private final IFileSystem fileSystem;
        private final FilePath path;

        public FileSystemResource(IFileSystem fileSystem, FilePath path) {
            this.fileSystem = fileSystem;
            this.path = path;
        }

        @Override
        public void dispose() {
            // Nothing to dispose
        }

        @Override
        public byte[] readBytes() throws IOException {
            return FileSystemUtil.readBytes(fileSystem, path);
        }

    }

    private static final class InMemoryResource implements IEncodedResource {

        private @Nullable byte[] bytes;

        public InMemoryResource(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public void dispose() {
            bytes = null;
        }

        @Override
        public byte[] readBytes() throws IOException {
            byte[] result = bytes;
            if (result == null) {
                throw new IOException("disposed");
            }
            return result;
        }

    }

    /**
     * Encoded resource that wraps a temp file. When the resource is disposed, the underlying temp file is
     * deleted.
     */
    private static final class TempFileResource implements IEncodedResource {

        private final File file;

        public TempFileResource(File file) {
            this.file = Checks.checkNotNull(file);
        }

        @Override
        public void dispose() {
            file.delete();
        }

        @Override
        public byte[] readBytes() throws IOException {
            try (InputStream in = new FileInputStream(file)) {
                return ByteStreams.toByteArray(in);
            }
        }

    }

}
