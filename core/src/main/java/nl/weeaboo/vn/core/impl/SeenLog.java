package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.lua2.io.ObjectDeserializer;
import nl.weeaboo.lua2.io.ObjectSerializer;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ISeenLog;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;

@CustomSerializable
final class SeenLog implements ISeenLog {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SeenLog.class);

    private final IEnvironment env;

    // Actual seen state is stored in a separate (shared) file
    private transient Map<MediaType, MediaSeen> mediaSeen;
    private transient Map<FilePath, ScriptSeen> scriptSeen;

    public SeenLog(IEnvironment env) {
        this.env = Checks.checkNotNull(env);

        initTransients();
    }

    private void initTransients() {
        mediaSeen = Maps.newEnumMap(MediaType.class);
        for (MediaType type : MediaType.values()) {
            mediaSeen.put(type, new MediaSeen());
        }

        scriptSeen = Maps.newHashMap();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    private ResourceId resolveResource(MediaType type, FilePath filename) {
        switch (type) {
        case IMAGE:
            return env.getImageModule().resolveResource(filename);
        case SOUND:
            return env.getSoundModule().resolveResource(filename);
        case VIDEO:
            return env.getVideoModule().resolveResource(filename);
        case SCRIPT:
            return env.getScriptLoader().resolveResource(filename);
        case OTHER:
            return null;
        default:
            LOG.warn("Unsupported resource type: {}", type);
            return null;
        }
    }

    @Override
    public boolean hasSeen(MediaType type, FilePath filename) {
        ResourceId resourceId = resolveResource(type, filename);
        return resourceId != null && hasSeen(resourceId);
    }

    @Override
    public boolean hasSeen(ResourceId resourceId) {
        return mediaSeen.get(resourceId.getType()).contains(resourceId);
    }

    @Override
    public boolean markSeen(ResourceId resourceId) {
        return mediaSeen.get(resourceId.getType()).addResource(resourceId);
    }

    @Override
    public void registerScriptFile(ResourceId resourceId, int numTextLines) {
        FilePath filePath = resourceId.getFilePath();
        ScriptSeen seen = scriptSeen.get(filePath);
        if (seen != null && seen.getNumTextLines() == numTextLines) {
            return; // ScriptSeen exists and is up-to-date
        }

        LOG.debug("Registered script file: {}", resourceId);

        seen = new ScriptSeen(numTextLines);
        scriptSeen.put(filePath, seen);
    }

    @Override
    public boolean hasSeenLine(FilePath filename, int lineNumber) {
        ResourceId resourceId = resolveResource(MediaType.SCRIPT, filename);
        return resourceId != null && hasSeenLine(resourceId, lineNumber);
    }

    @Override
    public boolean hasSeenLine(ResourceId resourceId, int lineNumber) {
        ScriptSeen seen = scriptSeen.get(resourceId.getFilePath());
        return seen != null && seen.hasSeenLine(lineNumber);
    }

    @Override
    public void markLineSeen(FilePath filename, int lineNumber) {
        ResourceId resourceId = resolveResource(MediaType.SCRIPT, filename);
        if (resourceId != null) {
            markLineSeen(resourceId, lineNumber);
        }
    }

    @Override
    public void markLineSeen(ResourceId resourceId, int lineNumber) {
        ScriptSeen seen = scriptSeen.get(resourceId.getFilePath());
        if (seen == null) {
            LOG.warn("Marking line of unknown script file: {}:{}", resourceId, lineNumber);
        } else {
            seen.markLineSeen(resourceId.getFilePath(), lineNumber);
        }
    }

    @Override
    public void save(SecureFileWriter sfw, FilePath path) throws IOException {
        LOG.info("Save seen log: {}", path);

        LuaSerializer ls = new LuaSerializer();
        ObjectSerializer out = ls.openSerializer(sfw.newOutputStream(path, false));
        try {
            out.writeInt(mediaSeen.size());
            for (Entry<MediaType, MediaSeen> entry : mediaSeen.entrySet()) {
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }

            out.writeInt(scriptSeen.size());
            for (Entry<FilePath, ScriptSeen> entry : scriptSeen.entrySet()) {
                out.writeUTF(entry.getKey().toString());
                out.writeObject(entry.getValue());
            }
        } finally {
            out.close();
        }
    }

    @Override
    public void load(SecureFileWriter sfw, FilePath path) throws IOException {
        LOG.info("Load seen log: {}", path);

        LuaSerializer ls = new LuaSerializer();
        ObjectDeserializer in = ls.openDeserializer(sfw.newInputStream(path));
        try {
            mediaSeen.clear();
            scriptSeen.clear();

            int mediaSeenSize = in.readInt();
            for (int n = 0; n < mediaSeenSize; n++) {
                MediaType key = (MediaType)in.readObject();
                MediaSeen value = (MediaSeen)in.readObject();
                mediaSeen.put(key, value);
            }

            int scriptSeenSize = in.readInt();
            for (int n = 0; n < scriptSeenSize; n++) {
                FilePath key = FilePath.of(in.readUTF());
                ScriptSeen value = (ScriptSeen)in.readObject();
                scriptSeen.put(key, value);
            }
        } catch (ClassNotFoundException e) {
            LOG.error("Invalid seen log: {}", path, e);
            throw new IOException(e);
        } finally {
            in.close();
        }
    }

    @LuaSerializable
    private static class MediaSeen implements Serializable {

        private static final long serialVersionUID = 1L;

        private final Set<FilePath> seenResources = Sets.newHashSet();

        public boolean addResource(ResourceId resourceId) {
            return seenResources.add(resourceId.getFilePath());
        }

        public boolean contains(ResourceId resourceId) {
            return seenResources.contains(resourceId.getFilePath());
        }

    }

    @LuaSerializable
    private static class ScriptSeen implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int numTextLines;
        private final BitSet seenLines;

        public ScriptSeen(int numTextLines) {
            this.numTextLines = numTextLines;
            this.seenLines = new BitSet(numTextLines);
        }

        private boolean inRange(int lineNumber) {
            return lineNumber >= 1 && lineNumber <= numTextLines;
        }

        public boolean hasSeenLine(int lineNumber) {
            if (!inRange(lineNumber)) {
                return false;
            }
            return seenLines.get(lineNumber - 1);
        }

        public void markLineSeen(FilePath file, int lineNumber) {
            if (!inRange(lineNumber)) {
                LOG.warn("Line number out of range: {}:{}", file, lineNumber);
            } else {
                seenLines.set(lineNumber - 1);
                LOG.trace("Mark line seen {}:{}", file, lineNumber);
            }
        }

        public int getNumTextLines() {
            return numTextLines;
        }
    }

}
