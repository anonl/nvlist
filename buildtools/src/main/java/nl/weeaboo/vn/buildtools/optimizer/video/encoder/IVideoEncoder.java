package nl.weeaboo.vn.buildtools.optimizer.video.encoder;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.optimizer.video.EncodedVideo;

/**
 * Video encoder.
 */
public interface IVideoEncoder {

    /**
     * Encodes a video file.
     *
     * @throws IOException If an I/O error occurs while writing the file.
     */
    EncodedVideo encode(EncodedVideo video) throws IOException;

}
