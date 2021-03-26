package nl.weeaboo.vn.buildtools.optimizer.sound.encoder;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.buildtools.optimizer.FfmpegEncoderTest;
import nl.weeaboo.vn.buildtools.optimizer.sound.EncodedSound;
import nl.weeaboo.vn.buildtools.optimizer.sound.SoundWithDef;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinition;

public final class FfmpegSoundEncoderTest extends FfmpegEncoderTest {

    @Override
    protected FfmpegSoundEncoder newEncoder() {
        return new FfmpegSoundEncoder(tempFileProvider);
    }

    @Test
    public void testEncode() throws IOException {
        SoundWithDef soundWithDef = new SoundWithDef(EMPTY_RESOURCE, new SoundDefinition("snd.mp3"));

        FfmpegSoundEncoder encoder = newEncoder();
        encoder.setProgram(getDummyExecutable());

        EncodedSound encoded = encoder.encode(soundWithDef);
        try {
            SoundDefinition def = encoded.getDef();
            Assert.assertEquals("snd.ogg", def.getFilename());
        } finally {
            encoded.dispose();
        }
    }
}
