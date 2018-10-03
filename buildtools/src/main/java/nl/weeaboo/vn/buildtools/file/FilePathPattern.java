package nl.weeaboo.vn.buildtools.file;

import java.util.regex.Pattern;

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
     * Matches paths using a simple glob-style pattern. The supported special characters are:
     * <table summary="Supported glob patterns">
     * <tr><td>*</td><td>Matches a sequence of zero or more characters excluding '/'</td></tr>
     * <tr><td>**</td><td>Matches a sequence of zero or more characters</td></tr>
     * </table>
     */
    public static FilePathPattern fromGlob(String globPattern) {
        return new GlobPattern(globPattern);
    }

    /**
     * @return {@code true} if the given path matches this pattern.
     */
    public abstract boolean matches(FilePath path);

    /**
     * @see #inFolder(FilePath)
     */
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

    /**
     * @see #fromGlob(String)
     */
    private static final class GlobPattern extends FilePathPattern {

        private final Pattern pattern;

        public GlobPattern(String glob) {
            pattern = Pattern.compile(globToRegex(glob));
        }

        private static String globToRegex(String glob) {
            StringBuilder sb = new StringBuilder();

            int[] codepoints = glob.codePoints().toArray();
            for (int n = 0; n < codepoints.length; n++) {
                if (codepoints[n] == '*') {
                    if (n + 1 < codepoints.length && codepoints[n + 1] == '*') {
                        // **
                        sb.append(".*");
                    } else {
                        // *
                        sb.append("[^/]*");
                    }
                } else {
                    sb.append(Pattern.quote(new String(codepoints, n, 1)));
                }
            }
            return sb.toString();
        }

        @Override
        public boolean matches(FilePath path) {
            return pattern.matcher(path.toString()).matches();
        }

    }

}
