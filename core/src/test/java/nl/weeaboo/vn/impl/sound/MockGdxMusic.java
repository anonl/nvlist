package nl.weeaboo.vn.impl.sound;

import java.io.Serializable;

import com.badlogic.gdx.audio.Music;

class MockGdxMusic implements Music, Serializable {

    private static final long serialVersionUID = 1L;

    private boolean playing;
    private boolean paused;
    private boolean looping;

    private float volume;

    private OnCompletionListener completionListener;

    public MockGdxMusic() {
        reset();
    }

    public final void reset() {
        playing = false;
        paused = false;
        looping = false;
        volume = 1f;
        completionListener = null;
    }

    @Override
    public void play() {
        playing = true;
        paused = false;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void stop() {
        paused = false;
        playing = false;
    }

    public void fireComplete() {
        if (completionListener != null) {
            completionListener.onCompletion(this);
        }
    }

    @Override
    public boolean isPlaying() {
        return playing && !paused;
    }

    @Override
    public void setLooping(boolean isLooping) {
        this.looping = isLooping;
    }

    @Override
    public boolean isLooping() {
        return looping;
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
    public void setPan(float pan, float volume) {
    }

    @Override
    public void setPosition(float position) {
    }

    @Override
    public float getPosition() {
        return 0;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.completionListener = listener;
    }

}
