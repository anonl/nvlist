package nl.weeaboo.vn.impl.video;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.video.VideoPlayer;

import nl.weeaboo.common.Dim;

public class MockGdxVideoPlayer implements VideoPlayer {

    private boolean playing;
    private boolean paused;
    private Dim videoSize = Dim.EMPTY;
    private Dim renderSize = Dim.EMPTY;
    private float volume = 1f;

    private FileHandle file;
    private VideoSizeListener sizeListener;
    private CompletionListener completionListener;

    @Override
    public void dispose() {
    }

    @Override
    public boolean render() {
        return true;
    }

    @Override
    public boolean isBuffered() {
        return true;
    }

    @Override
    public void resize(int width, int height) {
        renderSize = Dim.of(width, height);
    }

    /**
     * @return The current rendering size.
     * @see #resize(int, int)
     */
    public Dim getRenderSize() {
        return renderSize;
    }

    @Override
    public boolean play(FileHandle file) throws IOException {
        this.file = file;

        playing = true;
        paused = false;
        return true;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    /**
     * @return {@code true} if currently paused (but not stopped).
     */
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void stop() {
        playing = false;
        paused = false;
    }

    @Override
    public int getVideoWidth() {
        return videoSize.w;
    }

    @Override
    public int getVideoHeight() {
        return videoSize.h;
    }

    /**
     * Sets the video size and notifies the attached video size listener.
     */
    public void setVideoSize(int vw, int vh) {
        videoSize = Dim.of(vw, vh);

        if (sizeListener != null) {
            sizeListener.onVideoSize(vw, vh);
        }
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setOnVideoSizeListener(VideoSizeListener listener) {
        this.sizeListener = listener;
    }

    @Override
    public void setOnCompletionListener(CompletionListener listener) {
        this.completionListener = listener;
    }

    /**
     * Notifies the attached completion listener that the video has finished.
     */
    public void fireComplete() {
        if (completionListener != null) {
            completionListener.onCompletionListener(file);
        }
    }

}
