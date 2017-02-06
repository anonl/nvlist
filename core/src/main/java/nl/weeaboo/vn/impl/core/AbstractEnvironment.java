package nl.weeaboo.vn.impl.core;

import java.util.Arrays;
import java.util.Collection;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.INotifier;

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
