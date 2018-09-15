package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.buildtools.file.FilePathPattern;

public interface IOptimizerFileSet {

    /**
     * Marks the specified path as 'optimized'.
     */
    void markOptimized(FilePath originalRelativePath);

    /**
     * @return {@code true} if the specified path has previously been marked as 'optimized'.
     * @see #markOptimized(FilePath)
     */
    boolean isOptimized(FilePath path);

    /**
     * @return {@code true} if the given file path should be optimized.
     */
    boolean requiresOptimize(FilePath path);

    /**
     * Marks the given pattern as not requiring any optimization.
     *
     * @see #requiresOptimize(FilePath)
     */
    void exclude(FilePathPattern pattern);

}
