package nl.weeaboo.vn.buildtools.optimizer.sound.encoder;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.optimizer.sound.EncodedSound;
import nl.weeaboo.vn.buildtools.optimizer.sound.SoundWithDef;

public final class NoOpSoundEncoder implements ISoundEncoder {

    @Override
    public EncodedSound encode(SoundWithDef sound) throws IOException {
        return new EncodedSound(sound.getAudioData(), sound.getDef());
    }

}
