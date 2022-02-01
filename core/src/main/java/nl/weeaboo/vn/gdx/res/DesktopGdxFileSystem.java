package nl.weeaboo.vn.gdx.res;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.ZipFileArchive;

/**
 * File system implementation for desktop operating systems (Windows, Mac, Linux).
 */
public final class DesktopGdxFileSystem extends GdxFileSystem {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopGdxFileSystem.class);

    private final InternalGdxFileSystem internalFileSystem;

    // Cached file archives. Archives are searched in order.
    private transient @Nullable ImmutableList<ZipFileArchive> cachedFileArchives;

    public DesktopGdxFileSystem(File baseFolder) {
        this(baseFolder.toString().replace('\\', '/') + "/");
    }

    public DesktopGdxFileSystem(String internalFilePrefix) {
        super(true);

        internalFileSystem = new InternalGdxFileSystem(internalFilePrefix);
    }

    @Override
    protected void closeImpl() {
        super.closeImpl();

        internalFileSystem.close();

        if (cachedFileArchives != null) {
            for (IFileSystem fileSystem : cachedFileArchives) {
                fileSystem.close();
            }
        }
    }

    @Override
    public FileHandle resolve(String subPath) {
        return new DesktopFileHandle(this, FilePath.of(subPath));
    }

    private IFileSystem resolveFileSystem(FilePath path) {
        // Try to resolve the path as a regular file first
        if (internalFileSystem.exists(path)) {
            return internalFileSystem;
        }

        // If the path can't be resolved to a regular file, check the ZIP archives
        for (ZipFileArchive arc : getFileArchives()) {
            if (arc.getFileExists(path)) {
                return arc;
            }
        }

        // Use internal filesystem for invalid file handles
        return internalFileSystem;
    }

    private Collection<ZipFileArchive> getFileArchives() {
        if (cachedFileArchives == null) {
            ImmutableList.Builder<ZipFileArchive> archives = ImmutableList.builder();

            for (FileHandle arcFileHandle : getArchiveFiles()) {
                File file = arcFileHandle.file();
                if (!file.isFile()) {
                    // Abort at the first miss
                    LOG.info("Stopped opening archives: {}", file);
                    break;
                }

                ZipFileArchive arc = new ZipFileArchive();
                try {
                    arc.open(file);

                    LOG.info("Opened archive: {}", file);
                    archives.add(arc);
                } catch (IOException e) {
                    LOG.warn("Error opening archive: {}", file);
                    arc.close();
                }
            }

            cachedFileArchives = archives.build();
        }
        return cachedFileArchives;
    }

    /**
     * @return A sorted list of all top-level archive files in the internal file system.
     */
    private Iterable<FileHandle> getArchiveFiles() {
        Set<FileHandle> result = Sets.newTreeSet(new Comparator<FileHandle>() {
            @Override
            public int compare(FileHandle a, FileHandle b) {
                // Sort by name
                return a.name().compareTo(b.name());
            }
        });

        // Search current working directory
        for (FileHandle handle : Gdx.files.internal(".").list(".nvl")) {
            result.add(handle);
        }

        // Search internal filesystem (classpath, etc.)
        for (FileHandle handle : internalFileSystem.resolve(".").list(".nvl")) {
            result.add(handle);
        }

        LOG.info("Found {} archive files: {}", result.size(), result);

        return result;
    }

    private Set<FilePath> getChildren(FilePath path) {
        FileCollectOptions collectOpts = FileCollectOptions.files(path);
        collectOpts.collectFolders = true;
        collectOpts.recursive = false;

        Set<FilePath> result = Sets.newHashSet();
        Iterables.addAll(result, internalFileSystem.getFiles(collectOpts));
        for (ZipFileArchive arc : getFileArchives()) {
            Iterables.addAll(result, arc.getFiles(collectOpts));
        }
        return result;
    }

    private static final class DesktopFileHandle extends NonFileGdxFileHandle {

        private final DesktopGdxFileSystem fileSystem;
        private final FilePath path;

        public DesktopFileHandle(DesktopGdxFileSystem fileSystem, FilePath path) {
            super(path.toString(), FileType.Internal);

            this.fileSystem = Checks.checkNotNull(fileSystem);
            this.path = Checks.checkNotNull(path);
        }

        private IFileSystem resolveFileSystem() {
            return fileSystem.resolveFileSystem(path);
        }

        @Override
        public InputStream read() {
            try {
                return resolveFileSystem().openInputStream(path);
            } catch (IOException e) {
                throw gdxException(e);
            }
        }

        @Override
        public FileHandle child(String name) {
            return new DesktopFileHandle(fileSystem, path.resolve(name));
        }

        @Override
        public FileHandle parent() {
            FilePath parent = MoreObjects.firstNonNull(path.getParent(), FilePath.empty());
            return new DesktopFileHandle(fileSystem, parent);
        }

        @Override
        public Iterable<FileHandle> listChildren() {
            Set<FilePath> children = fileSystem.getChildren(path);
            return Iterables.transform(children, new Function<FilePath, DesktopFileHandle>() {
                @Override
                public DesktopFileHandle apply(FilePath childPath) {
                    return new DesktopFileHandle(fileSystem, childPath);
                }
            });
        }

        @Override
        public boolean isDirectory() {
            return resolveFileSystem().isFolder(path);
        }

        @Override
        public long length() {
            try {
                return resolveFileSystem().getFileSize(path);
            } catch (IOException e) {
                LOG.debug("Unable to determine file size for ({})", path);
                return 0L;
            }
        }

        @Override
        public boolean exists() {
            return resolveFileSystem().getFileExists(path);
        }

    }
}