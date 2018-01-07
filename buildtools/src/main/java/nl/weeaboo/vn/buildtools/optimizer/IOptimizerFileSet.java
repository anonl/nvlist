package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.filesystem.FilePath;

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

}
