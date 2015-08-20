package nl.weeaboo.vn.core.impl;

import nl.weeaboo.entity.Scene;
import nl.weeaboo.entity.World;
import nl.weeaboo.vn.NvlTestUtil;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.lua.LuaScriptContext;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;

@SuppressWarnings("serial")
public class TestContextFactory implements IContextFactory<Context> {

    public final LuaScriptEnv scriptEnv;
    public final BasicPartRegistry pr;
    public final World world;

    public TestContextFactory(LuaScriptEnv scriptEnv) {
        this.scriptEnv = scriptEnv;

        this.pr = new BasicPartRegistry();
        this.world = new World(pr);
    }

    protected Screen newScreen(Scene scene) {
        return NvlTestUtil.newScreen(pr, scene);
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
    public void setRenderEnv(IRenderEnv env) {
    }

}
