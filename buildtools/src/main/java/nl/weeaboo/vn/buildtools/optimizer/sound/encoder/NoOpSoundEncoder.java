package nl.weeaboo.vn.buildtools.optimizer.sound.encoder;

import nl.weeaboo.vn.buildtools.optimizer.sound.EncodedSound;
import nl.weeaboo.vn.buildtools.optimizer.sound.SoundWithDef;

public final class NoOpSoundEncoder implements ISoundEncoder {

    @Override
    public EncodedSound encode(SoundWithDef sound) {
        return new EncodedSound(sound.getAudioData(), sound.getDef());
    }

}
