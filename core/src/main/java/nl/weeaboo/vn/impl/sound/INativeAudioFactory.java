package nl.weeaboo.vn.impl.sound;

import java.io.IOException;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IResourceCache;
import nl.weeaboo.vn.impl.core.IPreloadHandler;

/**
 * Creates {@link INativeAudio} instances.
 */
interface INativeAudioFactory extends IPreloadHandler, IResourceCache {

    /** Creates a new {@link INativeAudio} instance. */
    INativeAudio createNativeAudio(FilePath filePath) throws IOException;

    Music newGdxMusic(FilePath filePath);

}
