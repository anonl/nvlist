package nl.weeaboo.vn.sound.impl;

import java.io.Serializable;

public interface INativeAudio extends Serializable {

    void play(int loops);

    void pause();

    void resume();

    void stop(int fadeOutMillis);

    boolean isPlaying();

    boolean isPaused();

    int getLoopsLeft();

    void setVolume(double volume);

}
