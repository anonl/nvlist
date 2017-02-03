package nl.weeaboo.vn.impl.core;

import java.io.IOException;

import nl.weeaboo.vn.core.Duration;
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
    public Duration getTotalPlayTime() {
        return Duration.ZERO;
    }

    @Override
    public Duration getIdleTime() {
        return Duration.ZERO;
    }

}
