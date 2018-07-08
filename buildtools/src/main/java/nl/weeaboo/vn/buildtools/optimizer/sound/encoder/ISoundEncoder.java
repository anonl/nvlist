package nl.weeaboo.vn.buildtools.optimizer.sound.encoder;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.optimizer.sound.EncodedSound;
import nl.weeaboo.vn.buildtools.optimizer.sound.SoundWithDef;

public interface ISoundEncoder {

    /**
     * Encodes an audio file.
     *
     * @throws IOException If an I/O error occurs while writing the file.
     */
    EncodedSound encode(SoundWithDef sound) throws IOException;

}
