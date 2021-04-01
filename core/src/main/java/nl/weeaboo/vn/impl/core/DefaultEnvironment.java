package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.signal.RenderEnvChangeSignal;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.stats.IStatsModule;
import nl.weeaboo.vn.text.ITextModule;
import nl.weeaboo.vn.video.IVideoModule;

/**
 * Default implementation of {@link IEnvironment}.
 */
@CustomSerializable
public class DefaultEnvironment extends AbstractEnvironment implements Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    IContextManager contextManager;
    LuaScriptEnv scriptEnv;

    IImageModule imageModule;
    ISoundModule soundModule;
    IVideoModule videoModule;
    ITextModule textModule;
    ISaveModule saveModule;
    IStatsModule statsModule;
    ISystemModule systemModule;

    IRenderEnv renderEnv;

    private boolean destroyed;

    public DefaultEnvironment() {
        initTransients();
    }

    private void initTransients() {
        getPrefStore().addPreferenceListener(this);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    @Override
    public void destroy() {
        if (destroyed) {
            return;
        }

        destroyed = true;

        for (IModule module : getModules()) {
            module.destroy();
        }

        ContextUtil.setCurrentContext(null);

        LuaRunState lrs = LuaRunState.getCurrent();
        if (lrs != null) {
            lrs.destroy();
        }

        getPrefStore().removePreferenceListener(this);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    private static <T> T checkSet(T object) {
        if (object == null) {
            throw new IllegalStateException("Incorrect initialization order, field is null");
        }
        return object;
    }

    @Override
    public IContextManager getContextManager() {
        return checkSet(contextManager);
    }

    @Override
    public LuaScriptEnv getScriptEnv() {
        return checkSet(scriptEnv);
    }

    @Override
    public IRenderEnv getRenderEnv() {
        return checkSet(renderEnv);
    }

    @Override
    public IImageModule getImageModule() {
        return checkSet(imageModule);
    }

    @Override
    public ISoundModule getSoundModule() {
        return checkSet(soundModule);
    }

    @Override
    public IVideoModule getVideoModule() {
        return checkSet(videoModule);
    }

    @Override
    public ITextModule getTextModule() {
        return checkSet(textModule);
    }

    @Override
    public ISaveModule getSaveModule() {
        return checkSet(saveModule);
    }

    @Override
    public ISystemModule getSystemModule() {
        return checkSet(systemModule);
    }

    @Override
    public IStatsModule getStatsModule() {
        return checkSet(statsModule);
    }

    @Override
    public void updateRenderEnv(Rect realClip, Dim realScreenSize) {
        IRenderEnv old = getRenderEnv();
        renderEnv = new RenderEnv(old.getVirtualSize(), realClip, realScreenSize);

        fireSignal(new RenderEnvChangeSignal(renderEnv));
    }

}
