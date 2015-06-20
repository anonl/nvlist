package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.ISystemEventHandler;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.lua.LuaScriptLoader;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.video.IVideoModule;

public class DefaultEnvironment extends AbstractEnvironment implements Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final BasicPartRegistry partRegistry;
    private final IContextManager contextManager;
    private final LuaScriptEnv scriptEnv;
    private final ISystemEventHandler systemEventHandler;

    private final IImageModule imageModule;
    private final ISoundModule soundModule;
    private final IVideoModule videoModule;
    private final ISaveModule saveModule;

    private IRenderEnv renderEnv;
    private boolean destroyed;

    public DefaultEnvironment(EnvironmentBuilder b) {
        this.partRegistry = Checks.checkNotNull(b.partRegistry);
        this.contextManager = Checks.checkNotNull(b.contextManager);
        this.scriptEnv = Checks.checkNotNull(b.scriptEnv);
        this.systemEventHandler = Checks.checkNotNull(b.systemEventHandler);

        this.imageModule = b.imageModule;
        this.soundModule = b.soundModule;
        this.videoModule = b.videoModule;
        this.saveModule = b.saveModule;

        this.renderEnv = Checks.checkNotNull(b.renderEnv);
    }

    @Override
    public void destroy() {
        destroyed = true;

        ContextUtil.setCurrentContext(null);
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public IContextManager getContextManager() {
        return contextManager;
    }

    @Override
    public BasicPartRegistry getPartRegistry() {
        return partRegistry;
    }

    @Override
    public LuaScriptEnv getScriptEnv() {
        return scriptEnv;
    }

    @Override
    public LuaScriptLoader getScriptLoader() {
        return (LuaScriptLoader)super.getScriptLoader();
    }

    @Override
    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

    @Override
    public ISystemEventHandler getSystemEventHandler() {
        return systemEventHandler;
    }

    @Override
    public IImageModule getImageModule() {
        return imageModule;
    }

    @Override
    public ISoundModule getSoundModule() {
        return soundModule;
    }

    @Override
    public IVideoModule getVideoModule() {
        return videoModule;
    }

    @Override
    public ISaveModule getSaveModule() {
        return saveModule;
    }

    @Override
    public void updateRenderEnv(Rect realClip, Dim realScreenSize) {
        IRenderEnv old = getRenderEnv();
        renderEnv = new RenderEnv(old.getVirtualSize(), realClip, realScreenSize, old.isTouchScreen());

        contextManager.setRenderEnv(getRenderEnv());
    }

}
