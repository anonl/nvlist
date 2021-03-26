package nl.weeaboo.vn.buildtools.optimizer.sound.encoder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

import nl.weeaboo.io.Filenames;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.optimizer.FfmpegEncoder;
import nl.weeaboo.vn.buildtools.optimizer.sound.EncodedSound;
import nl.weeaboo.vn.buildtools.optimizer.sound.SoundWithDef;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinitionBuilder;

/**
 * Audio encoder using ffmpeg.
 */
public final class FfmpegSoundEncoder extends FfmpegEncoder implements ISoundEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(FfmpegSoundEncoder.class);

    private static final String OUTPUT_EXT = "ogg";
    private static final String AUDIO_CODEC = "libvorbis";
    private static final String AUDIO_QUALITY = "3";

    public FfmpegSoundEncoder(ITempFileProvider tempFileProvider) {
        this(LOG, tempFileProvider);
    }

    @VisibleForTesting
    FfmpegSoundEncoder(Logger logger, ITempFileProvider tempFileProvider) {
        super(logger, tempFileProvider);
    }

    @Override
    public EncodedSound encode(SoundWithDef sound) throws IOException {
        IEncodedResource transcoded = super.encode(sound.getAudioData());

        SoundDefinitionBuilder soundDef = sound.getDef()
                .builder();
        soundDef.setFilename(Filenames.replaceExt(soundDef.getFilename(), OUTPUT_EXT));
        return new EncodedSound(transcoded, soundDef.build());
    }

    @Override
    protected List<String> getCodecArgs() {
        return Arrays.asList("-codec:a", AUDIO_CODEC, "-qscale:a", AUDIO_QUALITY);
    }

    @Override
    protected String getFileFormat() {
        return OUTPUT_EXT;
    }

}
