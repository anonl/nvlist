package nl.weeaboo.vn.buildtools.optimizer.sound.encoder;

import nl.weeaboo.vn.buildtools.optimizer.sound.EncodedSound;
import nl.weeaboo.vn.buildtools.optimizer.sound.SoundWithDef;

/**
 * No-op audio encoder which doesn't do anything.
 */
public final class NoOpSoundEncoder implements ISoundEncoder {

    @Override
    public EncodedSound encode(SoundWithDef sound) {
        return new EncodedSound(sound.getAudioData(), sound.getDef());
    }

}
