package nl.weeaboo.vn.video;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IRenderEnv;

public interface IVideo extends Serializable {

    /*
     * TODO: Define a shared interface between ISound and IVideo to reduce code duplication in tests, and to ensure that
     * their interfaces work as similar as possible.
     */

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
