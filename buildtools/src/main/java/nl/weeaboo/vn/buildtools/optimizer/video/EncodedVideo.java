package nl.weeaboo.vn.buildtools.optimizer.video;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.file.IEncodedResource;

public final class EncodedVideo implements IEncodedResource {

    private final String filename;
    private final IEncodedResource encodedVideo;

    public EncodedVideo(String filename, IEncodedResource encodedSound) {
        this.filename = filename;
        this.encodedVideo = encodedSound;
    }

    @Override
    public void dispose() {
        encodedVideo.dispose();
    }

    /**
     * Returns the filename of the encoded resource.
     */
    public String getFilename() {
        return filename;
    }

    @Override
    public byte[] readBytes() throws IOException {
        return encodedVideo.readBytes();
    }

}
