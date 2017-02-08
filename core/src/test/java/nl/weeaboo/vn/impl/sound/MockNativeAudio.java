package nl.weeaboo.vn.impl.sound;

public class MockNativeAudio implements INativeAudio {

    private static final long serialVersionUID = 1L;

    private int loopsLeft;
    private boolean paused;
    private double volume;

    @Override
    public void play(int loops) {
        loopsLeft = loops;
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
        stop(0);
    }

    @Override
    public void stop(int fadeOutMillis) {
        loopsLeft = 0;
        paused = false;
    }

    @Override
    public boolean isPlaying() {
        return loopsLeft != 0;
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
    public int getLoopsLeft() {
        return loopsLeft;
    }

    @Override
    public void setVolume(double volume) {
        this.volume = volume;
    }

    /**
     * Returns the current volume for this audio.
     * @see #setVolume(double)
     */
    public double getVolume() {
        return volume;
    }

}
