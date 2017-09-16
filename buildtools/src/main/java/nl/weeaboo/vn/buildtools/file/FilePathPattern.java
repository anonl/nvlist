package nl.weeaboo.vn.buildtools.file;

import nl.weeaboo.filesystem.FilePath;

/**
 * Matches files based on their relative path.
 */
public abstract class FilePathPattern {

    FilePathPattern() {
    }

    /**
     * Matches all paths equal to, or inside the given folder path.
     */
    public static FilePathPattern inFolder(FilePath folderPath) {
        return new FolderPrefixPattern(folderPath);
    }

    /**
     * @return {@code true} if the given path matches this pattern.
     */
    public abstract boolean matches(FilePath path);

    private static final class FolderPrefixPattern extends FilePathPattern {

        private FilePath folderPath;

        public FolderPrefixPattern(FilePath folderPath) {
            this.folderPath = folderPath;
        }

        @Override
        public boolean matches(FilePath path) {
            return path.startsWith(folderPath);
        }

    }

}
