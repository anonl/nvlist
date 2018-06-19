package nl.weeaboo.vn.impl.stats;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.StringBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.stats.IAnalytics;

final class Analytics implements IAnalytics {

    private static final long serialVersionUID = StatsImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(Analytics.class);

    // Actual state is stored in a separate (shared) file
    private transient SetMultimap<String, FilePath> loadsPerLine;

    public Analytics() {
        initTransients();
    }

    private void initTransients() {
        loadsPerLine = HashMultimap.create();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    @Override
    public void logResourceLoad(ResourceId resourceId, ResourceLoadInfo info) {
        for (String line : info.getCallStackTrace()) {
            loadsPerLine.put(line, resourceId.getFilePath());
        }
    }

    @Override
    public void load(SecureFileWriter sfw, FilePath path) throws IOException {
        initTransients();
    }

    @Override
    public void save(SecureFileWriter sfw, FilePath path) throws IOException {
        StringBuilder sb = new StringBuilder("[analytics]");
        for (String line : loadsPerLine.keySet()) {
            Set<FilePath> resources = loadsPerLine.get(line);
            sb.append("\n").append(line).append(": ").append(resources);
        }

        LOG.debug(sb.toString());
    }

}
