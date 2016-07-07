package nl.weeaboo.vn.core;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.vn.save.IStorage;

public interface IPlayTimer extends IUpdateable, Serializable {

    void load(IStorage storage) throws IOException;
    void save(IStorage storage) throws IOException;

    IDuration getTotalTime();
    IDuration getIdleTime();

}
