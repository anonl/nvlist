package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.lua2.io.ObjectDeserializer;
import nl.weeaboo.lua2.io.ObjectSerializer;
import nl.weeaboo.vn.core.IChoiceSeenLog;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IResourceSeenLog;
import nl.weeaboo.vn.core.IScriptSeenLog;
import nl.weeaboo.vn.core.ISeenLogHolder;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;

@CustomSerializable
final class SeenLog implements ISeenLogHolder, IResourceSeenLog, IScriptSeenLog, IChoiceSeenLog {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SeenLog.class);
    private static final int VERSION = 2;

    private final IEnvironment env;

    // Actual seen state is stored in a separate (shared) file
    private transient Map<MediaType, MediaSeen> mediaSeen;
    private transient Map<FilePath, IndexBasedSeen> scriptSeen;
    private transient Map<String, IndexBasedSeen> choiceSeen;

    public SeenLog(IEnvironment env) {
        this.env = Checks.checkNotNull(env);

        initTransients();
    }

    private void initTransients() {
        mediaSeen = newMediaSeen();

        scriptSeen = Maps.newHashMap();
        choiceSeen = Maps.newHashMap();
    }

    private static Map<MediaType, MediaSeen> newMediaSeen() {
        EnumMap<MediaType, MediaSeen> mediaSeen = Maps.newEnumMap(MediaType.class);
        for (MediaType type : MediaType.values()) {
            mediaSeen.put(type, new MediaSeen());
        }
        return mediaSeen;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    private @Nullable ResourceId resolveResource(MediaType type, FilePath filename) {
        switch (type) {
        case IMAGE:
            return env.getImageModule().resolveResource(filename);
        case SOUND:
            return env.getSoundModule().resolveResource(filename);
        case VIDEO:
            return env.getVideoModule().resolveResource(filename);
        case SCRIPT:
            return env.getScriptEnv().getScriptLoader().resolveResource(filename);
        case OTHER:
            return null;
        }

        LOG.warn("Unsupported resource type: {}", type);
        return null;
    }

    @Override
    public IResourceSeenLog getResourceLog() {
        return this;
    }

    @Override
    public IChoiceSeenLog getChoiceLog() {
        return this;
    }

    @Override
    public IScriptSeenLog getScriptLog() {
        return this;
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
        IndexBasedSeen seen = scriptSeen.get(filePath);
        if (seen != null && seen.getMaxIndex() == numTextLines) {
            return; // ScriptSeen exists and is up-to-date
        }

        LOG.debug("Registered script file: {}", resourceId);

        seen = new IndexBasedSeen(numTextLines);
        scriptSeen.put(filePath, seen);
    }

    @Override
    public boolean hasSeenLine(FilePath filename, int lineNumber) {
        ResourceId resourceId = resolveResource(MediaType.SCRIPT, filename);
        return resourceId != null && hasSeenLine(resourceId, lineNumber);
    }

    @Override
    public boolean hasSeenLine(ResourceId resourceId, int lineNumber) {
        IndexBasedSeen seen = scriptSeen.get(resourceId.getFilePath());
        return seen != null && seen.hasSeenIndex(lineNumber);
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
        IndexBasedSeen seen = scriptSeen.get(resourceId.getFilePath());
        if (seen == null) {
            LOG.warn("Marking line of unknown script file: {}:{}", resourceId, lineNumber);
        } else {
            seen.markIndexSeen(resourceId.toString(), lineNumber);
        }
    }

    @Override
    public void registerChoice(String uniqueChoiceId, int numOptions) {
        IndexBasedSeen seen = choiceSeen.get(uniqueChoiceId);
        if (seen != null && seen.getMaxIndex() == numOptions) {
            return; // ChoiceSeen exists and is up-to-date
        }

        LOG.debug("Registered choice: {}", uniqueChoiceId);

        seen = new IndexBasedSeen(numOptions);
        choiceSeen.put(uniqueChoiceId, seen);
    }

    @Override
    public boolean hasSelectedChoice(String uniqueChoiceId, int optionIndex) {
        IndexBasedSeen seen = choiceSeen.get(uniqueChoiceId);
        return seen != null && seen.hasSeenIndex(optionIndex);
    }

    @Override
    public void markChoiceSelected(String uniqueChoiceId, int optionIndex) {
        IndexBasedSeen seen = choiceSeen.get(uniqueChoiceId);
        if (seen != null) {
            seen.markIndexSeen(uniqueChoiceId, optionIndex);
        }
    }

    @Override
    public void save(SecureFileWriter sfw, FilePath path) throws IOException {
        LOG.info("Save seen log: {}", path);

        LuaSerializer ls = new LuaSerializer();
        ObjectSerializer out = ls.openSerializer(sfw.newOutputStream(path, false));
        try {
            out.writeInt(VERSION);

            out.writeInt(mediaSeen.size());
            for (Entry<MediaType, MediaSeen> entry : mediaSeen.entrySet()) {
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }

            out.writeInt(scriptSeen.size());
            for (Entry<FilePath, IndexBasedSeen> entry : scriptSeen.entrySet()) {
                out.writeUTF(entry.getKey().toString());
                out.writeObject(entry.getValue());
            }

            out.writeInt(choiceSeen.size());
            for (Entry<String, IndexBasedSeen> entry : choiceSeen.entrySet()) {
                out.writeUTF(entry.getKey());
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
            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException(StringUtil.formatRoot("Unsupported version (%s), expected (%s)",
                        version, VERSION));
            }

            mediaSeen = newMediaSeen();
            scriptSeen.clear();
            choiceSeen.clear();

            int mediaSeenSize = in.readInt();
            for (int n = 0; n < mediaSeenSize; n++) {
                MediaType key = (MediaType)in.readObject();
                MediaSeen value = (MediaSeen)in.readObject();
                mediaSeen.put(key, value);
            }

            int scriptSeenSize = in.readInt();
            for (int n = 0; n < scriptSeenSize; n++) {
                FilePath key = FilePath.of(in.readUTF());
                IndexBasedSeen value = (IndexBasedSeen)in.readObject();
                scriptSeen.put(key, value);
            }

            int choiceSeenSize = in.readInt();
            for (int n = 0; n < choiceSeenSize; n++) {
                String key = in.readUTF();
                IndexBasedSeen value = (IndexBasedSeen)in.readObject();
                choiceSeen.put(key, value);
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

        private final Set<String> seenResources = Sets.newHashSet();

        public boolean addResource(ResourceId resourceId) {
            return seenResources.add(resourceId.getFilePath().toString());
        }

        public boolean contains(ResourceId resourceId) {
            return seenResources.contains(resourceId.getFilePath().toString());
        }

    }

    @LuaSerializable
    private static class IndexBasedSeen implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int maxIndex;
        private final BitSet seenIndices;

        public IndexBasedSeen(int maxIndex) {
            this.maxIndex = maxIndex;
            this.seenIndices = new BitSet(maxIndex);
        }

        private boolean inRange(int index) {
            return index >= 1 && index <= maxIndex;
        }

        public boolean hasSeenIndex(int index) {
            if (!inRange(index)) {
                return false;
            }
            return seenIndices.get(index - 1);
        }

        public void markIndexSeen(String id, int index) {
            if (!inRange(index)) {
                LOG.warn("Index number out of range: {}:{}", id, index);
            } else {
                seenIndices.set(index - 1);
                LOG.trace("Mark index seen {}:{}", id, index);
            }
        }

        public int getMaxIndex() {
            return maxIndex;
        }
    }

}
