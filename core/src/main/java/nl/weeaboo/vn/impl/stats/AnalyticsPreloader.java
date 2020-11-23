package nl.weeaboo.vn.impl.stats;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IEnvironment;

final class AnalyticsPreloader implements IAnalyticsPreloader {

    private static final long serialVersionUID = StatsImpl.serialVersionUID;

    private final IEnvironment env;

    public AnalyticsPreloader(IEnvironment env) {
        this.env = env;
    }

    @Override
    public void preloadImage(FilePath path) {
        env.getImageModule().preload(path);
    }

    @Override
    public void preloadSound(FilePath path) {
        env.getSoundModule().preload(path);
    }

}
