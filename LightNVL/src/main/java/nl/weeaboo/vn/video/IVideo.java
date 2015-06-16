package nl.weeaboo.vn.video;

import java.io.IOException;
import java.io.Serializable;

public interface IVideo extends Serializable {

    // === Functions ===========================================================
    public void prepare() throws IOException;
    public void start() throws IOException;
    public void pause();
    public void resume();
    public void stop();

    // === Getters =============================================================
    public boolean isPrepared();
    public boolean isPlaying();
    public boolean isPaused();
    public boolean isStopped();

    public String getVideoPath();
    public double getPrivateVolume();
    public double getMasterVolume();
    public double getVolume();

    // === Setters =============================================================
    public void setVideoPath(String filename);
    public void setPrivateVolume(double v);
    public void setMasterVolume(double v);

}
