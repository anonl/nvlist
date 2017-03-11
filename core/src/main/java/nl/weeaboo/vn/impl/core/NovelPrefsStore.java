package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.prefsstore.AbstractPreferenceStore;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.reflect.ReflectUtil;
import nl.weeaboo.settings.PropertiesUtil;

public final class NovelPrefsStore extends AbstractPreferenceStore {

    private static final Logger LOG = LoggerFactory.getLogger(NovelPrefsStore.class);

    private static final FilePath CONSTANTS_FILENAME = FilePath.of("config.ini");
    private static final FilePath DEFAULTS_FILENAME = FilePath.of("prefs.default.ini");
    private static final FilePath VARIABLES_FILENAME = FilePath.of("prefs.ini");

    private final IFileSystem resourceFileSystem;
    private final IWritableFileSystem outputSystem;

    public NovelPrefsStore(IFileSystem resFS, IWritableFileSystem outFS) {
        this.resourceFileSystem = Checks.checkNotNull(resFS);
        this.outputSystem = Checks.checkNotNull(outFS);
    }

    @Override
    public void loadVariables() throws IOException {
        initConsts(load(resourceFileSystem, CONSTANTS_FILENAME));
        setAll(load(resourceFileSystem, DEFAULTS_FILENAME));
        setAll(load(outputSystem, VARIABLES_FILENAME));
    }

    private Map<String, String> load(IFileSystem fileSystem, FilePath filename) throws IOException {
        if (!fileSystem.getFileExists(filename)) {
            return Collections.emptyMap();
        }

        InputStream in = fileSystem.openInputStream(filename);
        try {
            return PropertiesUtil.load(in);
        } finally {
            in.close();
        }
    }

    @Override
    public void saveVariables() throws IOException {
        OutputStream out = outputSystem.openOutputStream(VARIABLES_FILENAME, false);
        try {
            PropertiesUtil.save(out, getVariables());
        } finally {
            out.close();
        }
    }

    /**
     * @return All preferences declared in static fields
     */
    public static List<Preference<?>> getDeclaredPrefs(Class<?> clazz) {
        List<Preference<?>> result = Lists.newArrayList();

        try {
            for (Preference<?> pref : ReflectUtil.getConstants(clazz, Preference.class).values()) {
                LOG.trace("Found declared preference: {}", pref.getKey());

                result.add(pref);
            }
        } catch (IllegalAccessException e) {
            LOG.warn("Error retrieving attributes from preference holder: {}", clazz, e);
        }
        return result;
    }

}
