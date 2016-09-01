package nl.weeaboo.vn.video;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IRenderEnv;

public interface IVideo extends Serializable {

    void prepare() throws IOException;
    void start() throws IOException;
    void pause();
    void resume();
    void stop();

    void render();

    boolean isPrepared();
    boolean isPlaying();
    boolean isPaused();
    boolean isStopped();

    FilePath getFilename();
    double getPrivateVolume();
    double getMasterVolume();
    double getVolume();

    void setPrivateVolume(double v);
    void setMasterVolume(double v);
    void setRenderEnv(IRenderEnv renderEnv);

}
