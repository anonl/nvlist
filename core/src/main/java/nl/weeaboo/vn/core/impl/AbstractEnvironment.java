package nl.weeaboo.vn.core.impl;

import java.util.Arrays;
import java.util.Collection;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.settings.Preference;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IModule;
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

    @Override
    public IPreferenceStore getPrefStore() {
        return StaticEnvironment.PREFS.get();
    }

    @Override
    public <T> T getPref(Preference<T> pref) {
        return getPrefStore().get(pref);
    }

    @Override
    public boolean isDebug() {
        return getPref(NovelPrefs.SCRIPT_DEBUG);
    }

    @Override
    public IScriptLoader getScriptLoader() {
        return getScriptEnv().getScriptLoader();
    }

    @Override
    public Collection<IModule> getModules() {
        return Arrays.asList(
            getImageModule(),
            getSoundModule(),
            getVideoModule(),
            getTextModule(),
            getSystemModule(),
            getSaveModule()); // Destroy save module last, so other modules can still save during destroy()
    }

}
