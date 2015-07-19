package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.IResourceLoadLog;
import nl.weeaboo.vn.core.ISystemEventHandler;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.video.IVideoModule;

public class DefaultEnvironment extends AbstractEnvironment implements Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    BasicPartRegistry partRegistry;
    IContextManager contextManager;
    LuaScriptEnv scriptEnv;
    ISystemEventHandler systemEventHandler;
    IResourceLoadLog resourceLoadLog;

    IImageModule imageModule;
    ISoundModule soundModule;
    IVideoModule videoModule;
    ISaveModule saveModule;

    IRenderEnv renderEnv;

    private boolean destroyed;

    @Override
    public void destroy() {
        destroyed = true;

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
    public BasicPartRegistry getPartRegistry() {
        return checkSet(partRegistry);
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
    public ISystemEventHandler getSystemEventHandler() {
        return checkSet(systemEventHandler);
    }

    @Override
    public IResourceLoadLog getResourceLoadLog() {
        return checkSet(resourceLoadLog);
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
    public ISaveModule getSaveModule() {
        return checkSet(saveModule);
    }

    @Override
    public void updateRenderEnv(Rect realClip, Dim realScreenSize) {
        IRenderEnv old = getRenderEnv();
        renderEnv = new RenderEnv(old.getVirtualSize(), realClip, realScreenSize, old.isTouchScreen());

        contextManager.setRenderEnv(getRenderEnv());
    }

}
