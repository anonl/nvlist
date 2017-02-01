package nl.weeaboo.vn.impl.video;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.impl.video.INativeVideo;

public class MockNativeVideo implements INativeVideo {

    private static final long serialVersionUID = 1L;

    private boolean prepared;
    private boolean playing;
    private boolean paused;
    private double volume = 1.0;

    private final AtomicInteger renderCount = new AtomicInteger();
    private IRenderEnv renderEnv;

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
        renderCount.incrementAndGet();
    }

    public int consumeRenderCount() {
        return renderCount.getAndSet(0);
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
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }

    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

    @Override
    public void setRenderEnv(IRenderEnv renderEnv) {
        this.renderEnv = renderEnv;
    }

}
