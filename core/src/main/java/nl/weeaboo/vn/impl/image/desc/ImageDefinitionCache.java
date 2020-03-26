package nl.weeaboo.vn.impl.image.desc;

import java.io.IOException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.core.ResourceMetaDataCache;

/**
 * Cache for {@link IImageDefinition}.
 */
public final class ImageDefinitionCache extends ResourceMetaDataCache<IImageDefinition> {

    private final IFileSystem fileSystem;

    public ImageDefinitionCache(IFileSystem fileSystem) {
        this.fileSystem = Checks.checkNotNull(fileSystem);
    }

    @Override
    protected void loadFolderMetadata(FilePath folder) throws IOException {
        FilePath jsonPath = folder.resolve(IImageDefinition.IMG_DEF_FILE);
        String json = FileSystemUtil.readString(fileSystem, jsonPath);

        // Load JSON and add image definitions to the cache
        for (IImageDefinition imageDef : ImageDefinitionIO.deserialize(json)) {
            addToCache(folder.resolve(imageDef.getFilename()), imageDef);
        }
    }
}
