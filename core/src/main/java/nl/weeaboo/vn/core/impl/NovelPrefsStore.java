package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.settings.AbstractPreferenceStore;
import nl.weeaboo.settings.PropertiesUtil;

public final class NovelPrefsStore extends AbstractPreferenceStore {

    private static final FilePath CONSTANTS_FILENAME = FilePath.of("config.ini");
    private static final FilePath DEFAULTS_FILENAME = FilePath.of("prefs.default.ini");
    private static final FilePath VARIABLES_FILENAME = FilePath.of("prefs.ini");

    private final IWritableFileSystem fileSystem;

    public NovelPrefsStore(IWritableFileSystem fs) {
        this.fileSystem = Checks.checkNotNull(fs);
	}

    @Override
    public void loadVariables() throws IOException {
        initConsts(load(CONSTANTS_FILENAME));
        setAll(load(DEFAULTS_FILENAME));
        setAll(load(VARIABLES_FILENAME));
    }

    private Map<String, String> load(FilePath filename) throws IOException {
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
        OutputStream out = fileSystem.openOutputStream(VARIABLES_FILENAME, false);
        try {
            PropertiesUtil.save(out, getVariables());
        } finally {
            out.close();
        }
    }

}
