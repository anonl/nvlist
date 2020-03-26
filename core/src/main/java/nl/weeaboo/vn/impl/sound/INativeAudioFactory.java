package nl.weeaboo.vn.impl.sound;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;

/**
 * Creates {@link INativeAudio} instances.
 */
interface INativeAudioFactory extends Serializable {

    /** Creates a new {@link INativeAudio} instance. */
    INativeAudio createNativeAudio(FilePath filePath) throws IOException;

}
