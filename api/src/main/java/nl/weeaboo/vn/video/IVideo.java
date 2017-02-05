package nl.weeaboo.vn.video;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IStreamingMedia;

public interface IVideo extends Serializable, IStreamingMedia {

    void prepare() throws IOException;
    void start() throws IOException;

    void render();

    boolean isPrepared();

    FilePath getFilename();
    double getPrivateVolume();
    double getMasterVolume();
    double getVolume();

    void setPrivateVolume(double v);
    void setMasterVolume(double v);
    void setRenderEnv(IRenderEnv renderEnv);

}
