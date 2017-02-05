package nl.weeaboo.vn.impl.video;

import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IStreamingMedia;

public interface INativeVideo extends Serializable, IStreamingMedia {

    void prepare();
    void play() throws VideoPlayerInitException, IOException;

    void render();

    boolean isPrepared();

    void setVolume(double volume);
    void setRenderEnv(IRenderEnv renderEnv);

}
