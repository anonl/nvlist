package nl.weeaboo.vn.impl.sound.desc;

import java.io.IOException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.impl.core.ResourceMetaDataCache;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

/**
 * Cache for {@link ISoundDefinition}.
 */
public final class SoundDefinitionCache extends ResourceMetaDataCache<ISoundDefinition> {

    private final IFileSystem fileSystem;

    public SoundDefinitionCache(IFileSystem fileSystem) {
        this.fileSystem = Checks.checkNotNull(fileSystem);
    }

    @Override
    protected void loadFolderMetadata(FilePath folder) throws IOException {
        FilePath jsonPath = folder.resolve(ISoundDefinition.SND_DEF_FILE);
        String json = FileSystemUtil.readString(fileSystem, jsonPath);

        // Load JSON and add sound definitions to the cache
        for (ISoundDefinition soundDef : SoundDefinitionIO.deserialize(json)) {
            addToCache(folder.resolve(soundDef.getFilename()), soundDef);
        }
    }
}
