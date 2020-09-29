package nl.weeaboo.vn.impl.sound;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinitionCache;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

final class SoundResourceLoader extends FileResourceLoader {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private transient @Nullable SoundDefinitionCache cachedSoundDefs;

    public SoundResourceLoader(IEnvironment env) {
        super(env, MediaType.SOUND);

        setAutoFileExts(SoundModule.getSupportedFileExts().toArray(new String[0]));
    }

    /**
     * Returns the sound definition corresponding to the specified audio file, or {@code null} if it doesn't
     * exist or doesn't have a sound definition.
     */
    @CheckForNull
    public final ISoundDefinition getSoundDef(FilePath filePath) {
        return getSoundDefinitions().getMetaData(filePath);
    }

    private SoundDefinitionCache getSoundDefinitions() {
        SoundDefinitionCache result = cachedSoundDefs;
        if (result == null) {
            result = new SoundDefinitionCache(getFileSystem());
            cachedSoundDefs = result;
        }
        return result;
    }

    @Override
    protected void onResourceFolderChanged() {
        super.onResourceFolderChanged();

        cachedSoundDefs = null;
    }

}
