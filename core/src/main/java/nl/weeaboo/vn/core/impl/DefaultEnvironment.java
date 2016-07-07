package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.IPlayTimer;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IResourceLoadLog;
import nl.weeaboo.vn.core.ISeenLog;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.text.ITextModule;
import nl.weeaboo.vn.video.IVideoModule;

public class DefaultEnvironment extends AbstractEnvironment implements Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    IContextManager contextManager;
    LuaScriptEnv scriptEnv;
    IResourceLoadLog resourceLoadLog;
    ISeenLog seenLog;
    IPlayTimer playTimer;

    IImageModule imageModule;
    ISoundModule soundModule;
    IVideoModule videoModule;
    ITextModule textModule;
    ISaveModule saveModule;
    ISystemModule systemModule;

    IRenderEnv renderEnv;

    private boolean destroyed;

    @Override
    public void destroy() {
        destroyed = true;

        for (IModule module : getModules()) {
            module.destroy();
        }

        ContextUtil.setCurrentContext(null);
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
    public IResourceLoadLog getResourceLoadLog() {
        return checkSet(resourceLoadLog);
    }

    @Override
    public ISeenLog getSeenLog() {
        return checkSet(seenLog);
    }

    @Override
    public IPlayTimer getPlayTimer() {
        return checkSet(playTimer);
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
    public void updateRenderEnv(Rect realClip, Dim realScreenSize) {
        IRenderEnv old = getRenderEnv();
        ISystemEnv systemEnv = StaticEnvironment.SYSTEM_ENV.get();
        renderEnv = new RenderEnv(old.getVirtualSize(), realClip, realScreenSize, systemEnv.isTouchScreen());

        contextManager.setRenderEnv(getRenderEnv());
    }

}
