package nl.weeaboo.vn.buildtools.optimizer.video.encoder;

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
import nl.weeaboo.vn.buildtools.optimizer.video.EncodedVideo;

/**
 * Video encoder using ffmpeg.
 */
public final class FfmpegVideoEncoder extends FfmpegEncoder implements IVideoEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(FfmpegVideoEncoder.class);

    private static final String OUTPUT_EXT = "webm";
    private static final String VIDEO_CODEC = "libvpx";
    private static final String VIDEO_QUALITY = "3";
    private static final String AUDIO_CODEC = "libvorbis";
    private static final String AUDIO_QUALITY = "3";

    public FfmpegVideoEncoder(ITempFileProvider tempFileProvider) {
        this(LOG, tempFileProvider);
    }

    @VisibleForTesting
    FfmpegVideoEncoder(Logger logger, ITempFileProvider tempFileProvider) {
        super(logger, tempFileProvider);
    }

    @Override
    public EncodedVideo encode(EncodedVideo video) throws IOException {
        IEncodedResource transcoded = super.encode(video);

        String newFilename = Filenames.replaceExt(video.getFilename(), OUTPUT_EXT);
        return new EncodedVideo(newFilename, transcoded);
    }

    @Override
    protected List<String> getCodecArgs() {
        return Arrays.asList("-codec:v", VIDEO_CODEC, "-qscale:v", VIDEO_QUALITY,
                "-codec:a", AUDIO_CODEC, "-qscale:a", AUDIO_QUALITY);
    }

    @Override
    protected String getFileFormat() {
        return OUTPUT_EXT;
    }

}
