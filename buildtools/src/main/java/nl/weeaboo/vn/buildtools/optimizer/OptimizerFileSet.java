package nl.weeaboo.vn.buildtools.optimizer;

import java.util.Set;

import com.google.common.collect.Sets;

import nl.weeaboo.filesystem.FilePath;

final class OptimizerFileSet implements IOptimizerFileSet {

    private final Set<FilePath> optimized = Sets.newHashSet();

    @Override
    public void markOptimized(FilePath originalRelativePath) {
        optimized.add(originalRelativePath);
    }

    @Override
    public boolean isOptimized(FilePath path) {
        return optimized.contains(path);
    }

}
