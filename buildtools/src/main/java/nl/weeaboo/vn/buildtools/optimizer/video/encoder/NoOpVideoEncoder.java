package nl.weeaboo.vn.buildtools.optimizer.video.encoder;

import nl.weeaboo.vn.buildtools.optimizer.video.EncodedVideo;

/**
 * No-op video encoder which doesn't do anything.
 */
public final class NoOpVideoEncoder implements IVideoEncoder {

    @Override
    public EncodedVideo encode(EncodedVideo video) {
        return video;
    }

}
