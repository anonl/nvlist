package nl.weeaboo.vn.buildtools.optimizer.video.encoder;

import nl.weeaboo.vn.buildtools.optimizer.video.EncodedVideo;

public final class NoOpVideoEncoder implements IVideoEncoder {

    @Override
    public EncodedVideo encode(EncodedVideo video) {
        return video;
    }

}
