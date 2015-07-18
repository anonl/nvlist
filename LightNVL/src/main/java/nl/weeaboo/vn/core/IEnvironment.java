package nl.weeaboo.vn.core;

import java.util.Collection;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.entity.PartRegistry;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.settings.Preference;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.IScriptEnv;
import nl.weeaboo.vn.script.IScriptLoader;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.video.IVideoModule;

/** Wrapper object that contains global engine state. */
public interface IEnvironment extends IDestructible {

    IFileSystem getFileSystem();
    IWritableFileSystem getOutputFileSystem();
    INotifier getNotifier();
    boolean isDebug();
    <T> T getPref(Preference<T> pref);

    IContextManager getContextManager();
    PartRegistry getPartRegistry();
    IScriptEnv getScriptEnv();
    IScriptLoader getScriptLoader();
    IRenderEnv getRenderEnv();
    ISystemEventHandler getSystemEventHandler();

    IImageModule getImageModule();
    ISoundModule getSoundModule();
    IVideoModule getVideoModule();
    ISaveModule getSaveModule();
    Collection<IModule> getModules();

    /**
     * @see IRenderEnv
     */
    void updateRenderEnv(Rect realClip, Dim realScreenSize);

}
