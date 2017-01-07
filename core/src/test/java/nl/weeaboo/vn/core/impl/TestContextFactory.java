package nl.weeaboo.vn.core.impl;

import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.scene.impl.Screen;
import nl.weeaboo.vn.script.impl.lua.LuaScriptContext;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.test.CoreTestUtil;

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
