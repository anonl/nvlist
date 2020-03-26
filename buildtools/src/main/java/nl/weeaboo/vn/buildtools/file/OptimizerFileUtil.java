package nl.weeaboo.vn.buildtools.file;

import java.util.Collection;
import java.util.TreeSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import nl.weeaboo.filesystem.FilePath;

/**
 * Various functions for working with files.
 */
public final class OptimizerFileUtil {

    /**
     * Returns a filtered view of the files, containing only those files that match one of the given valid
     * file extensions.
     */
    public static Iterable<FilePath> filterByExts(Iterable<FilePath> files, Collection<String> validExts) {
        // Use a tree set so we can match in a case-insensitive way
        TreeSet<String> validExtsSet = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);
        validExtsSet.addAll(validExts);

        return Iterables.filter(files, path -> validExtsSet.contains(path.getExt()));
    }

}
