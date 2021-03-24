package nl.weeaboo.vn.buildtools.optimizer.video.encoder;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.vn.buildtools.optimizer.FfmpegEncoderTest;
import nl.weeaboo.vn.buildtools.optimizer.video.EncodedVideo;

public final class FfmpegVideoEncoderTest extends FfmpegEncoderTest {

    @Override
    protected FfmpegVideoEncoder newEncoder() {
        return new FfmpegVideoEncoder(tempFileProvider);
    }

    @Test
    public void testEncode() throws IOException {
        EncodedVideo video = new EncodedVideo("video.mkv", EMPTY_RESOURCE);

        FfmpegVideoEncoder encoder = newEncoder();
        encoder.setProgram(getDummyExecutable());

        EncodedVideo encoded = encoder.encode(video);
        try {
            Assert.assertEquals("video.webm", encoded.getFilename());
        } finally {
            encoded.dispose();
        }
    }
}
