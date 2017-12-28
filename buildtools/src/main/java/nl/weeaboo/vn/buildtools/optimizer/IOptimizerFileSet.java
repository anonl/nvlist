package nl.weeaboo.vn.buildtools.optimizer;

import nl.weeaboo.filesystem.FilePath;

public interface IOptimizerFileSet {

    /**
     * Marks the specified path as 'optimized'.
     */
    void markOptimized(FilePath originalRelativePath);

    boolean isOptimized(FilePath path);

}
