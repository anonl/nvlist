package nl.weeaboo.vn.sound.impl.desc;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.impl.JsonUtil;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;
import nl.weeaboo.vn.sound.impl.desc.SoundDefinitionFileJson.SoundDefinitionJson;

/** Helper class for reading/writing {@link SoundDefinition} objects */
public final class SoundDefinitionIO {

    private static final Logger LOG = LoggerFactory.getLogger(SoundDefinitionIO.class);
    private static final String VERSION = "1.0";

    private SoundDefinitionIO() {
    }

    public static Map<FilePath, ISoundDefinition> fromFileSystem(IFileSystem fileSystem, FilePath rootFolder)
            throws IOException, SaveFormatException
    {
        Map<FilePath, ISoundDefinition> result = Maps.newHashMap();
        for (FilePath folder : getFolders(fileSystem, rootFolder)) {
            FilePath path = folder.resolve("snd.json");
            if (!fileSystem.getFileExists(path)) {
                continue;
            }

            for (SoundDefinition soundDef : deserialize(FileSystemUtil.readString(fileSystem, path))) {
                FilePath relPath = folder.resolve(soundDef.getFilename());
                result.put(relPath, soundDef);
            }
        }
        return result;
    }

    private static Iterable<FilePath> getFolders(IFileSystem fileSystem, FilePath rootFolder) throws IOException {
        return Iterables.concat(ImmutableList.of(rootFolder),
                fileSystem.getFiles(FileCollectOptions.folders(rootFolder)));
    }

    public static String serialize(Collection<SoundDefinition> soundDefs) {
        SoundDefinitionFileJson fileJson = new SoundDefinitionFileJson();
        fileJson.version = VERSION;
        fileJson.sounds = new SoundDefinitionJson[soundDefs.size()];
        int t = 0;
        for (SoundDefinition soundDef : soundDefs) {
            fileJson.sounds[t++] = encodeJson(soundDef);
        }
        return JsonUtil.toJson(fileJson);
    }

    public static Collection<SoundDefinition> deserialize(String string) throws SaveFormatException {
        SoundDefinitionFileJson fileJson = JsonUtil.fromJson(SoundDefinitionFileJson.class, string);
        if (!VERSION.equals(fileJson.version)) {
            throw new SaveFormatException("Expected " + VERSION + ", was " + fileJson.version);
        }

        List<SoundDefinition> result = Lists.newArrayList();
        for (SoundDefinitionJson soundDefJson : fileJson.sounds) {
            try {
                result.add(decodeJson(soundDefJson));
            } catch (RuntimeException re) {
                LOG.error("Invalid sound definition: {}", soundDefJson.file, re);
            }
        }
        return result;
    }

    private static SoundDefinitionJson encodeJson(SoundDefinition soundDef) {
        SoundDefinitionJson soundDefJson = new SoundDefinitionJson();
        soundDefJson.file = soundDef.getFilename().toString();
        soundDefJson.displayName = soundDef.getDisplayName();
        return soundDefJson;
    }

    private static SoundDefinition decodeJson(SoundDefinitionJson soundDefJson) {
        String filename = soundDefJson.file;
        String displayName = soundDefJson.displayName;
        return new SoundDefinition(filename, displayName);
    }

}
