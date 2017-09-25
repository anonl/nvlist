package nl.weeaboo.vn.buildtools.file;

import java.io.IOException;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;

public final class EncodedResource {

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

        private byte[] bytes;

        public InMemoryResource(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public void dispose() {
            bytes = null;
        }

        @Override
        public byte[] readBytes() throws IOException {
            if (bytes == null) {
                throw new IOException("disposed");
            }

            return bytes;
        }

    }

}
