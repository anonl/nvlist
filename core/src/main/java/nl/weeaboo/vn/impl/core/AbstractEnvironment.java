package nl.weeaboo.vn.impl.core;

import java.util.Arrays;
import java.util.Collection;

import com.badlogic.gdx.assets.AssetManager;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.prefsstore.IPreferenceListener;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.INotifier;
import nl.weeaboo.vn.gdx.res.GdxCleaner;
import nl.weeaboo.vn.gdx.res.NativeMemoryTracker;
import nl.weeaboo.vn.stats.IResourceLoadLog;

abstract class AbstractEnvironment implements IEnvironment, IPreferenceListener {

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
    public final IPreferenceStore getPrefStore() {
        return StaticEnvironment.PREFS.get();
    }

    @Override
    public IResourceLoadLog getResourceLoadLog() {
        return getStatsModule().getResourceLoadLog();
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
            getStatsModule(),
            getSaveModule()); // Destroy save module last, so other modules can still save during destroy()
    }

    @Override
    public void update() {
        getSaveModule().processSaveLoadRequests();

        // Perform synchronous part of asset loading
        AssetManager assetManager = StaticEnvironment.ASSET_MANAGER.getIfPresent();
        if (assetManager != null) {
            assetManager.update();
        }

        for (IModule module : getModules()) {
            module.update();
        }

        getContextManager().update();
    }

    /** Called when the global preferences change. */
    @Override
    public <T> void onPreferenceChanged(Preference<T> pref, T oldValue, T newValue) {
        IPreferenceStore prefsStore = getPrefStore();
        for (IModule module : getModules()) {
            module.onPrefsChanged(prefsStore);
        }
        getContextManager().onPrefsChanged(prefsStore);
    }

    @Override
    public void clearCaches() {
        for (IModule module : getModules()) {
            module.clearCaches();
        }

        System.gc();
        GdxCleaner.get().cleanUp();

        System.gc();
        NativeMemoryTracker.get().cleanUp();
    }

}
