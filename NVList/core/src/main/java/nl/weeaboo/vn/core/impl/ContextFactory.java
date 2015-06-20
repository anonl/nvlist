package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.entity.Scene;
import nl.weeaboo.entity.World;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.lua.LuaScriptContext;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;

public class ContextFactory implements IContextFactory<Context> {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    public final LuaScriptEnv scriptEnv;
    public final BasicPartRegistry pr;
    public final World world;

    private IRenderEnv renderEnv;

    public ContextFactory(LuaScriptEnv scriptEnv, IRenderEnv renderEnv) {
        this.scriptEnv = scriptEnv;
        this.renderEnv = renderEnv;

        this.pr = new BasicPartRegistry();
        this.world = new World(pr);
    }

    protected Screen newScreen(Scene scene) {
        Rect2D rect = Rect2D.of(0, 0, renderEnv.getWidth(), renderEnv.getHeight());
        return new Screen(scene, rect, pr, renderEnv);
    }

    @Override
    public Context newContext() {
        Scene scene = world.createScene();

        ContextArgs contextArgs = new ContextArgs();
        contextArgs.scene = scene;
        contextArgs.screen = newScreen(scene);
        contextArgs.drawablePart = pr.drawable;
        contextArgs.scriptContext = newScriptContext();

        return new Context(contextArgs);
    }

    protected IScriptContext newScriptContext() {
        if (scriptEnv == null) {
            return null;
        }
        return new LuaScriptContext(scriptEnv);
    }

    @Override
    public void setRenderEnv(IRenderEnv renderEnv) {
        this.renderEnv = Checks.checkNotNull(renderEnv);
    }

}
