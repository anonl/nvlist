package nl.weeaboo.vn.impl.core;

import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.impl.core.Context;
import nl.weeaboo.vn.impl.core.ContextArgs;
import nl.weeaboo.vn.impl.core.SkipState;
import nl.weeaboo.vn.impl.scene.Screen;
import nl.weeaboo.vn.impl.script.lua.LuaScriptContext;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.render.IRenderEnv;

@SuppressWarnings("serial")
public class TestContextFactory implements IContextFactory<Context> {

    public final LuaScriptEnv scriptEnv;

    public TestContextFactory(LuaScriptEnv scriptEnv) {
        this.scriptEnv = scriptEnv;
    }

    @Override
    public Context newContext() {
        ContextArgs contextArgs = new ContextArgs();
        contextArgs.skipState = new SkipState();
        contextArgs.screen = newScreen();
        contextArgs.scriptContext = newScriptContext();

        return new Context(contextArgs);
    }

    protected Screen newScreen() {
        return CoreTestUtil.newScreen();
    }

    protected LuaScriptContext newScriptContext() {
        return new LuaScriptContext(scriptEnv);
    }

    @Override
    public void setRenderEnv(IRenderEnv env) {
    }

}
