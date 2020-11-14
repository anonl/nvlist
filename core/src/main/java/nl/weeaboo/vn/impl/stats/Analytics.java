package nl.weeaboo.vn.impl.stats;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.lua2.io.ObjectDeserializer;
import nl.weeaboo.lua2.io.ObjectSerializer;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.core.LruSet;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.stats.IAnalytics;

final class Analytics implements IAnalytics {

    private static final long serialVersionUID = StatsImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(Analytics.class);

    private static final int VERSION = 2;
    private static final int LOOKAHEAD_LINES = 20;

    private final IEnvironment env;
    private final IAnalyticsPreloader preloader;

    // Transient because the actual state is stored in a separate (shared) file
    private transient Map<FileLine, LineStats> loadsPerLine;

    private transient LruSet<FileLine> recentPreloads;

    public Analytics(IEnvironment env) {
        this(env, new AnalyticsPreloader(env));
    }

    @VisibleForTesting
    Analytics(IEnvironment env, IAnalyticsPreloader preloader) {
        this.env = Checks.checkNotNull(env);
        this.preloader = Checks.checkNotNull(preloader);

        initTransients();
    }

    private void initTransients() {
        loadsPerLine = Maps.newHashMap();
        recentPreloads = new LruSet<>(10);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    @Override
    public void update() {
        // Handle preloads for every active thread
        for (IContext active : env.getContextManager().getActiveContexts()) {
            for (IScriptThread thread : active.getScriptContext().getThreads()) {
                List<String> stackTrace = thread.getStackTrace();
                FileLine lvnLine = LuaScriptUtil.getNearestLvnSrcloc(stackTrace);
                // Use recentPreloads as a rate-limiter to avoid preloading the same line over and over again
                if (lvnLine != null) {
                    handlePreloads(lvnLine);
                }
            }
        }
    }

    @VisibleForTesting
    void handlePreloads(FileLine lvnLine) {
        if (recentPreloads.add(lvnLine)) {
            doHandlePreloads(lvnLine);
        }
    }

    private void doHandlePreloads(FileLine lvnLine) {
        for (LineStats lineStats : getUpcomingLines(lvnLine)) {
            for (FilePath path : lineStats.imagesLoaded) {
                preloader.preloadImage(path);
            }
            for (FilePath path : lineStats.soundsLoaded) {
                preloader.preloadSound(path);
            }
        }
    }

    private Iterable<LineStats> getUpcomingLines(FileLine current) {
        FilePath path = current.getFilePath();
        int startLine = current.getLineNumber();
        if (startLine <= 0) {
            // Unknown line number -- abort
            return ImmutableList.of();
        }

        // Get LineStats for upcoming lines
        List<LineStats> result = Lists.newArrayList();
        for (int offset = 1; offset <= LOOKAHEAD_LINES; offset++) {
            FileLine fileLine = new FileLine(path, startLine + offset);
            LineStats lineStats = loadsPerLine.get(fileLine);
            if (lineStats != null) {
                result.add(lineStats);
            }
        }
        return result;
    }

    @Override
    public void logLoad(ResourceId resourceId, ResourceLoadInfo info) {
        FileLine lvnLine = LuaScriptUtil.getNearestLvnSrcloc(info.getCallStackTrace());
        if (lvnLine == null) {
            return;
        }

        LineStats stats = getOrCreateLineStats(lvnLine);
        stats.logResourceLoad(info);
    }

    private LineStats getOrCreateLineStats(FileLine fileLine) {
        LineStats stats = loadsPerLine.get(fileLine);
        if (stats == null) {
            stats = new LineStats();
            loadsPerLine.put(fileLine, stats);
        }
        return stats;
    }

    @Override
    public void load(SecureFileWriter sfw, FilePath path) throws IOException {
        LOG.info("Loading analytics: {}", path);

        LuaSerializer ls = new LuaSerializer();
        ObjectDeserializer in = ls.openDeserializer(sfw.newInputStream(path));
        try {
            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException(StringUtil.formatRoot("Unsupported version (%s), expected (%s)",
                        version, VERSION));
            }

            initTransients();

            int mapSize = in.readInt();
            for (int n = 0; n < mapSize; n++) {
                FileLine fileLine = FileLine.fromString(in.readUTF());
                LineStats lineStats = (LineStats)in.readUnshared();
                loadsPerLine.put(fileLine, lineStats);
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Invalid analytics file: " + path, e);
        } finally {
            in.close();
        }
    }

    @Override
    public void save(SecureFileWriter sfw, FilePath path) throws IOException {
        LOG.info("Saving analytics: {}", path);

        LuaSerializer ls = new LuaSerializer();
        ObjectSerializer out = ls.openSerializer(sfw.newOutputStream(path, false));
        try {
            out.writeInt(VERSION);

            out.writeInt(loadsPerLine.size());
            for (Map.Entry<FileLine, LineStats> entry : loadsPerLine.entrySet()) {
                out.writeUTF(entry.getKey().toString());
                out.writeUnshared(entry.getValue());
            }
        } finally {
            out.close();
        }
    }

    /** Per-line analytics-related state. */
    @LuaSerializable
    private static final class LineStats implements Externalizable {

        private static final long serialVersionUID = StatsImpl.serialVersionUID;

        private final Set<FilePath> imagesLoaded = Sets.newHashSet();
        private final Set<FilePath> soundsLoaded = Sets.newHashSet();

        // No-arg constructor is required by Externalizable interface
        public LineStats() {
        }

        public void logResourceLoad(ResourceLoadInfo info) {
            // Get file path without sub-resource id: name.ext#sub -> name.ext
            FilePath path = ResourceId.extractFilePath(info.getPath().toString());
            if (info.getMediaType() == MediaType.IMAGE) {
                imagesLoaded.add(path);
            } else if (info.getMediaType() == MediaType.SOUND) {
                soundsLoaded.add(path);
            }
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(imagesLoaded.size());
            for (FilePath path : imagesLoaded) {
                out.writeUTF(path.toString());
            }

            out.writeInt(soundsLoaded.size());
            for (FilePath path : soundsLoaded) {
                out.writeUTF(path.toString());
            }
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            int imagesLoadedSize = in.readInt();
            for (int n = 0; n < imagesLoadedSize; n++) {
                imagesLoaded.add(FilePath.of(in.readUTF()));
            }

            int soundsLoadedSize = in.readInt();
            for (int n = 0; n < soundsLoadedSize; n++) {
                soundsLoaded.add(FilePath.of(in.readUTF()));
            }
        }

    }

}
