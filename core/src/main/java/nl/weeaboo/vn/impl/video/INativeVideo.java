package nl.weeaboo.vn.impl.video;

import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.vn.core.IRenderEnv;

public interface INativeVideo extends Serializable {

    void prepare();
    void play() throws VideoPlayerInitException, IOException;
    void pause();
    void resume();
    void stop();

    void render();

    boolean isPrepared();
    boolean isPlaying();
    boolean isPaused();

    void setVolume(double volume);
    void setRenderEnv(IRenderEnv renderEnv);

}
