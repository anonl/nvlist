package nl.weeaboo.vn.video;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.vn.core.IRenderEnv;

public interface IVideo extends Serializable {

    // === Functions ===========================================================
    void prepare() throws IOException;
    void start() throws IOException;
    void pause();
    void resume();
    void stop();

    void render();

    // === Getters =============================================================
    boolean isPrepared();
    boolean isPlaying();
    boolean isPaused();
    boolean isStopped();

    String getFilename();
    double getPrivateVolume();
    double getMasterVolume();
    double getVolume();

    // === Setters =============================================================
    void setPrivateVolume(double v);
    void setMasterVolume(double v);
    void setRenderEnv(IRenderEnv renderEnv);

}
