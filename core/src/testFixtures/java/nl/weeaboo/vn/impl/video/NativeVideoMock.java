package nl.weeaboo.vn.impl.video;

import java.util.concurrent.atomic.AtomicInteger;

import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.RenderEnvChangeSignal;

final class NativeVideoMock implements INativeVideo {

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
    public void play() {
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

    /**
     * Gets and clear the internal render counter. This counter tracks the number of times that the {@link #render()}
     * method is called.
     */
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
    public boolean isStopped() {
        return !isPlaying() && !isPaused();
    }

    @Override
    public void setVolume(double volume) {
        this.volume = volume;
    }

    /**
     * @return The current volume.
     * @see #setVolume(double)
     */
    public double getVolume() {
        return volume;
    }

    /**
     * @return The current render env, or {@code null} if no render env was set.
     */
    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

    @Override
    public void handleSignal(ISignal signal) {
        if (signal.isUnhandled(RenderEnvChangeSignal.class)) {
            renderEnv = ((RenderEnvChangeSignal)signal).getRenderEnv();
        }
    }

}
