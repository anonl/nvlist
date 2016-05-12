package nl.weeaboo.vn.core.impl;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ISeenLog;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;

final class SeenLog implements ISeenLog {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SeenLog.class);

    private final IEnvironment env;
    private final Map<MediaType, MediaSeen> mediaSeen = Maps.newEnumMap(MediaType.class);
    private final Map<String, ScriptSeen> scriptSeen = Maps.newHashMap();

    public SeenLog(IEnvironment env) {
        this.env = Checks.checkNotNull(env);

        for (MediaType type : MediaType.values()) {
            mediaSeen.put(type, new MediaSeen());
        }
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
    public boolean hasSeenLine(ResourceId resourceId, int lineNumber) {
        ScriptSeen seen = scriptSeen.get(resourceId.getCanonicalFilename());
        return seen != null && seen.hasSeenLine(lineNumber);
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

    private static class MediaSeen implements Serializable {

        private static final long serialVersionUID = CoreImpl.serialVersionUID;

        private final Set<String> seenResources = Sets.newHashSet();

        public boolean addResource(ResourceId resourceId) {
            return seenResources.add(resourceId.getCanonicalFilename());
        }

        public boolean contains(ResourceId resourceId) {
            return seenResources.contains(resourceId.getCanonicalFilename());
        }

    }

    private static class ScriptSeen implements Serializable {

        private static final long serialVersionUID = CoreImpl.serialVersionUID;

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
            }
        }

        public int getNumTextLines() {
            return numTextLines;
        }

    }

}
