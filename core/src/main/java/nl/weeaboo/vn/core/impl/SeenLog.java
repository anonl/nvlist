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
    private transient Map<String, ScriptSeen> scriptSeen;

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

    private ResourceId resolveResource(MediaType type, String filename) {
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
    public boolean hasSeen(MediaType type, String filename) {
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
        String filename = resourceId.getCanonicalFilename();
        ScriptSeen seen = scriptSeen.get(filename);
        if (seen != null && seen.getNumTextLines() == numTextLines) {
            return; // ScriptSeen exists and is up-to-date
        }

        LOG.debug("Registered script file: {}", resourceId);

        seen = new ScriptSeen(filename, numTextLines);
        scriptSeen.put(filename, seen);
    }

    @Override
    public boolean hasSeenLine(String filename, int lineNumber) {
        ResourceId resourceId = resolveResource(MediaType.SCRIPT, filename);
        return resourceId != null && hasSeenLine(resourceId, lineNumber);
    }

    @Override
    public boolean hasSeenLine(ResourceId resourceId, int lineNumber) {
        ScriptSeen seen = scriptSeen.get(resourceId.getCanonicalFilename());
        return seen != null && seen.hasSeenLine(lineNumber);
    }

    @Override
    public void markLineSeen(String filename, int lineNumber) {
        ResourceId resourceId = resolveResource(MediaType.SCRIPT, filename);
        if (resourceId != null) {
            markLineSeen(resourceId, lineNumber);
        }
    }

    @Override
    public void markLineSeen(ResourceId resourceId, int lineNumber) {
        ScriptSeen seen = scriptSeen.get(resourceId.getCanonicalFilename());
        if (seen == null) {
            LOG.warn("Marking line of unknown script file: {}:{}", resourceId, lineNumber);
        } else {
            seen.markLineSeen(lineNumber);
        }
    }

    @Override
    public void save(SecureFileWriter sfw, String filename) throws IOException {
        LOG.info("Save seen log: {}", filename);

        LuaSerializer ls = new LuaSerializer();
        ObjectSerializer out = ls.openSerializer(sfw.newOutputStream(filename, false));
        try {
            out.writeInt(mediaSeen.size());
            for (Entry<MediaType, MediaSeen> entry : mediaSeen.entrySet()) {
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }

            out.writeInt(scriptSeen.size());
            for (Entry<String, ScriptSeen> entry : scriptSeen.entrySet()) {
                out.writeUTF(entry.getKey());
                out.writeObject(entry.getValue());
            }
        } finally {
            out.close();
        }
    }

    @Override
    public void load(SecureFileWriter sfw, String filename) throws IOException {
        LOG.info("Load seen log: {}", filename);

        LuaSerializer ls = new LuaSerializer();
        ObjectDeserializer in = ls.openDeserializer(sfw.newInputStream(filename));
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
                String key = in.readUTF();
                ScriptSeen value = (ScriptSeen)in.readObject();
                scriptSeen.put(key, value);
            }
        } catch (ClassNotFoundException e) {
            LOG.error("Invalid seen log: {}", filename, e);
            throw new IOException(e);
        } finally {
            in.close();
        }
    }

    @LuaSerializable
    private static class MediaSeen implements Serializable {

        private static final long serialVersionUID = 1L;

        private final Set<String> seenResources = Sets.newHashSet();

        public boolean addResource(ResourceId resourceId) {
            return seenResources.add(resourceId.getCanonicalFilename());
        }

        public boolean contains(ResourceId resourceId) {
            return seenResources.contains(resourceId.getCanonicalFilename());
        }

    }

    @LuaSerializable
    private static class ScriptSeen implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String filename;
        private final int numTextLines;
        private final BitSet seenLines;

        public ScriptSeen(String filename, int numTextLines) {
            this.filename = Checks.checkNotNull(filename);
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

        public void markLineSeen(int lineNumber) {
            if (!inRange(lineNumber)) {
                LOG.warn("Line number out of range: {}:{}", filename, lineNumber);
            } else {
                seenLines.set(lineNumber - 1);
                LOG.trace("Mark line seen {}:{}", filename, lineNumber);
            }
        }

        public int getNumTextLines() {
            return numTextLines;
        }

    }

}
