package nl.weeaboo.vn.core.impl;

import java.io.IOException;

import nl.weeaboo.vn.core.IDuration;
import nl.weeaboo.vn.core.IPlayTimer;
import nl.weeaboo.vn.save.IStorage;

public class PlayTimerStub implements IPlayTimer {

    private static final long serialVersionUID = 1L;

    @Override
    public void update() {
    }

    @Override
    public void load(IStorage storage) throws IOException {
    }

    @Override
    public void save(IStorage storage) throws IOException {
    }

    @Override
    public IDuration getTotalTime() {
        return Duration.ZERO;
    }

    @Override
    public IDuration getIdleTime() {
        return Duration.ZERO;
    }

}
