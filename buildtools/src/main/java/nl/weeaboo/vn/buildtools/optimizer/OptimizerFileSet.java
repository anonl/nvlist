package nl.weeaboo.vn.buildtools.optimizer;

import java.util.Set;

import com.google.common.collect.Sets;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.buildtools.file.FilePathPattern;

final class OptimizerFileSet implements IOptimizerFileSet {

    private final Set<FilePath> optimized = Sets.newHashSet();

    private final Set<FilePathPattern> exclusions = Sets.newHashSet();

    @Override
    public void markOptimized(FilePath originalRelativePath) {
        optimized.add(originalRelativePath);
    }

    @Override
    public boolean isOptimized(FilePath path) {
        return optimized.contains(path);
    }

    @Override
    public void exclude(FilePathPattern pattern) {
        exclusions.add(pattern);
    }

    @Override
    public boolean requiresOptimize(FilePath path) {
        // If the file matches an exclusion pattern, we don't need to optimize it
        for (FilePathPattern pattern : exclusions) {
            if (pattern.matches(path)) {
                return false;
            }
        }

        return true;
    }

}
