package nl.weeaboo.vn.core.impl;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.settings.Preference;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INotifier;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.script.IScriptLoader;

abstract class AbstractEnvironment implements IEnvironment {

    @Override
    public IFileSystem getFileSystem() {
        return StaticEnvironment.FILE_SYSTEM.get();
    }

    @Override
    public IWritableFileSystem getOutputFileSystem() {
        return StaticEnvironment.OUTPUT_FILE_SYSTEM.get();
    }

    @Override
    public INotifier getNotifier() {
        return StaticEnvironment.NOTIFIER.get();
    }

    private static IPreferenceStore getPrefs() {
        return StaticEnvironment.PREFS.get();
    }

    @Override
    public <T> T getPref(Preference<T> pref) {
        return getPrefs().get(pref);
    }

    @Override
    public boolean isDebug() {
        return getPref(NovelPrefs.SCRIPT_DEBUG);
    }

    @Override
    public IScriptLoader getScriptLoader() {
        return getScriptEnv().getScriptLoader();
    }

}
