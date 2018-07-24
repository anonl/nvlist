package nl.weeaboo.vn.buildtools.optimizer.video.encoder;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.optimizer.video.EncodedVideo;

public final class NoOpVideoEncoder implements IVideoEncoder {

    @Override
    public EncodedVideo encode(EncodedVideo video) throws IOException {
        return video;
    }

}
