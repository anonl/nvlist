package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptContext;

public interface IContext extends Serializable, IDestructible {

    void addContextListener(IContextListener contextListener);

    void removeContextListener(IContextListener contextListener);

    boolean isActive();

    void updateScreen();

    void updateScripts();

    IScreen getScreen();

    IScriptContext getScriptContext();

    ISkipState getSkipState();

    void setRenderEnv(IRenderEnv env);

}
