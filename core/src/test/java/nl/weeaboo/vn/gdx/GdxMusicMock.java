package nl.weeaboo.vn.gdx;

import com.badlogic.gdx.audio.Music;

final class GdxMusicMock implements Music {

    private boolean playing;
    private boolean paused;
    private float volume = 1f;
    private float position = 1f;
    private boolean isLooping = false;

    @Override
    public void play() {
        playing = true;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void stop() {
        playing = false;
        paused = false;
    }

    @Override
    public boolean isPlaying() {
        return playing && !paused;
    }

    @Override
    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }

    @Override
    public boolean isLooping() {
        return isLooping;
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
        this.position = position;
    }

    @Override
    public float getPosition() {
        return position;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
    }

}
