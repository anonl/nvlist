package nl.weeaboo.vn.impl.stats;

import java.io.IOException;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.stats.IAnalytics;

final class Analytics implements IAnalytics {

    private static final long serialVersionUID = StatsImpl.serialVersionUID;

    @Override
    public void load(SecureFileWriter sfw, FilePath path) throws IOException {

    }

    @Override
    public void save(SecureFileWriter sfw, FilePath path) throws IOException {
    }

}
