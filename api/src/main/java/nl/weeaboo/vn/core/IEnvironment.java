package nl.weeaboo.vn.core;

import java.util.Collection;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.IScriptEnv;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.stats.IResourceLoadLog;
import nl.weeaboo.vn.stats.IStatsModule;
import nl.weeaboo.vn.text.ITextModule;
import nl.weeaboo.vn.video.IVideoModule;

/** Wrapper object that contains global engine state. */
public interface IEnvironment extends IDestructible, IUpdateable {

    /** The filesystem used for reading resources. */
    IFileSystem getFileSystem();

    /** The filesystem used for storing save data. */
    IWritableFileSystem getOutputFileSystem();

    /** A notifier for displaying messages to the user. */
    INotifier getNotifier();

    /** The global preferences. */
    IPreferenceStore getPrefStore();

    /**
     * Fetches a preference value from the global preferences.
     * @see #getPrefStore()
     */
    <T> T getPref(Preference<T> pref);

    /** The global context manager. */
    IContextManager getContextManager();

    /** The global scripting environment. */
    IScriptEnv getScriptEnv();

    /** Information about the current rendering environment. */
    IRenderEnv getRenderEnv();

    /**
     * The global resource loading log.
     * @see IStatsModule#getResourceLoadLog()
     */
    IResourceLoadLog getResourceLoadLog();

    // --- Start modules -----------------------------------------------------------------------------------------------

    /** The global image module. */
    IImageModule getImageModule();

    /** The global audio module. */
    ISoundModule getSoundModule();

    /** The global video module. */
    IVideoModule getVideoModule();

    /** The global text module. */
    ITextModule getTextModule();

    /** The global save module. */
    ISaveModule getSaveModule();

    /** The global stats module. */
    IStatsModule getStatsModule();

    /** The global system module. */
    ISystemModule getSystemModule();

    /** Returns a read-only view containing all available modules. */
    Collection<IModule> getModules();

    // --- End modules -----------------------------------------------------------------------------------------------

    /**
     * @see IRenderEnv
     */
    void updateRenderEnv(Rect realClip, Dim realScreenSize);

    /**
     * Forwards a signal to all relevant handlers.
     */
    void fireSignal(ISignal signal);

    /**
     * Clears (resource) caches. This is automatically done during a restart.
     */
    void clearCaches();
}
