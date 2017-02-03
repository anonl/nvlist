package nl.weeaboo.vn.android;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;

public class AndroidAssetFileSystem extends GdxFileSystem {

    private static final Logger LOG = LoggerFactory.getLogger(AndroidAssetFileSystem.class);

    private transient ImmutableMap<FilePath, FileIndexEntry> fileIndex;

    public AndroidAssetFileSystem() {
        super(true);
    }

    private FileIndexEntry getIndexEntry(FilePath path) {
        if (fileIndex == null) {
            fileIndex = ImmutableMap.copyOf(loadFileIndex());
        }
        return fileIndex.get(path);
    }

    private Map<FilePath, FileIndexEntry> loadFileIndex() {
        Map<FilePath, FileIndexEntry> map = Maps.newHashMap();

        FileIndexEntry rootEntry = new FileIndexEntry(FilePath.empty());
        map.put(FilePath.empty(), rootEntry);

        LOG.debug("Looking for assets.list");

        FileHandle assetsFile = resolve("assets.list");
        if (assetsFile.exists()) {
            final Stopwatch sw = Stopwatch.createStarted();

            String assetsList = assetsFile.readString("UTF-8");
            List<String> pathStrings = Lists.newArrayList(Splitter.on('\n')
                    .trimResults()
                    .omitEmptyStrings()
                    .split(assetsList));
            Collections.sort(pathStrings);

            LOG.debug("Found assets.list with {} records", pathStrings.size());

            for (String pathString : pathStrings) {
                FilePath path = FilePath.of(pathString);
                FileIndexEntry entry = new FileIndexEntry(path);

                // Add entry
                map.put(path, entry);
                if (pathString.endsWith("/")) {
                    // Also register folders under without their ending '/'.
                    FilePath stripped = FilePath.of(pathString.substring(0, pathString.length() - 1));
                    map.put(stripped, entry);
                }

                // Add to parent entry (should exist because we process paths in sorted order)
                FileIndexEntry parentEntry = map.get(path.getParent());
                if (parentEntry == null) {
                    // File is a direct child of the root
                    parentEntry = rootEntry;
                }
                parentEntry.addChild(path);
            }

            LOG.debug("Processing assets.list took {}", sw);
        }
        return map;
    }

    @Override
    protected boolean exists(FilePath path) {
        return getIndexEntry(path) != null;
    }

    @Override
    protected Iterable<FilePath> list(FilePath path, FileHandle handle) {
        FileIndexEntry metaData = getIndexEntry(path);
        if (metaData == null) {
            return ImmutableList.of();
        }
        return metaData.getChildren();
    }

    @Override
    public FileHandle resolve(String fileName) {
        return Gdx.files.internal(fileName);
    }

    private static final class FileIndexEntry {

        private final FilePath relpath;
        private List<FilePath> children;

        public FileIndexEntry(FilePath relpath) {
            this.relpath = Checks.checkNotNull(relpath);
        }

        @Override
        public String toString() {
            return relpath.toString();
        }

        public void addChild(FilePath child) {
            if (children == null) {
                children = Lists.newArrayList();
            }
            children.add(child);
        }

        public List<FilePath> getChildren() {
            if (children == null) {
                return ImmutableList.of();
            }
            return children;
        }

    }

}
