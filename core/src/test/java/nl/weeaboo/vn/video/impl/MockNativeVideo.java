package nl.weeaboo.vn.video.impl;

import java.io.IOException;

import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IRenderEnv;

public class MockNativeVideo implements INativeVideo {

    private static final long serialVersionUID = 1L;

    private final FilePath path;

    private boolean prepared;
    private boolean playing;
    private boolean paused;

    public MockNativeVideo(FilePath path) {
        this.path = Checks.checkNotNull(path);
    }

    public FilePath getPath() {
        return path;
    }

    @Override
    public void prepare() {
        prepared = true;
    }

    @Override
    public void play() throws VideoPlayerInitException, IOException {
        playing = true;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void stop() {
        playing = false;
    }

    @Override
    public void render() {
    }

    @Override
    public boolean isPrepared() {
        return prepared;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void setVolume(double volume) {
    }

    @Override
    public void setRenderEnv(IRenderEnv renderEnv) {
    }

}
