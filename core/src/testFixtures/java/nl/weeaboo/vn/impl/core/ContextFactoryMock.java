package nl.weeaboo.vn.impl.core;

import nl.weeaboo.vn.core.ContextListener;
import nl.weeaboo.vn.core.IContextFactory;
import nl.weeaboo.vn.impl.scene.Screen;
import nl.weeaboo.vn.impl.script.lua.LuaScriptContext;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.script.IScriptThread;

@SuppressWarnings("serial")
public class ContextFactoryMock implements IContextFactory<Context> {

    public final LuaScriptEnv scriptEnv;

    public ContextFactoryMock(LuaScriptEnv scriptEnv) {
        this.scriptEnv = scriptEnv;
    }

    @Override
    public Context newContext() {
        ContextArgs contextArgs = new ContextArgs();
        contextArgs.skipState = new SkipState();
        contextArgs.screen = newScreen();
        contextArgs.scriptContext = newScriptContext();

        Context context = new Context(contextArgs);
        context.addContextListener(new ContextListener() {
            @Override
            public void onScriptException(IScriptThread thread, Exception exception) {
                throw new AssertionError("Error in script: " + thread, exception);
            }
        });
        return context;
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
