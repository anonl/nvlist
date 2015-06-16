package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.ISystemEventHandler;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.IScriptEnv;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.video.IVideoModule;

public class EnvironmentBuilder extends AbstractEnvironment {

    public BasicPartRegistry partRegistry;
    public IContextManager contextManager;
    public LuaScriptEnv scriptEnv;
    public ISystemEventHandler systemEventHandler;

    public IImageModule imageModule;
    public ISoundModule soundModule;
    public IVideoModule videoModule;
    public ISaveModule saveModule;

    public IRenderEnv renderEnv;

    @Override
    public void destroy() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isDestroyed() {
        return false;
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
    public IScriptEnv getScriptEnv() {
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
        throw new RuntimeException("Not implemented");
    }

}
