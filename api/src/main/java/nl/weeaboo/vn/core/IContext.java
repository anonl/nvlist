package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.script.IScriptContext;

public interface IContext extends Serializable, IDestructible, IEntityContainer {

    public void addContextListener(IContextListener contextListener);

    public void removeContextListener(IContextListener contextListener);

    public boolean isActive();

    public void updateScreen();

    public void updateScripts();

	public IScreen getScreen();

	public IScriptContext getScriptContext();

    public void setRenderEnv(IRenderEnv env);

    public Entity findEntity(int entityId);

}
