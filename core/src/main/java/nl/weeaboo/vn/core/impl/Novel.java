package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;

public class Novel extends AbstractNovel {

    private static final Logger LOG = LoggerFactory.getLogger(Novel.class);

    // --- Note: This class uses manual serialization ---
    private transient boolean isStarted;
    // --- Note: This class uses manual serialization ---

    public Novel(EnvironmentFactory envFactory) {
        super(envFactory);
    }

    @Override
    public void readAttributes(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readAttributes(in);
    }

    @Override
    public void writeAttributes(ObjectOutput out) throws IOException {
        super.writeAttributes(out);
    }

    @Override
    public void start(String mainFunctionName) throws InitException {
        super.start(mainFunctionName);

        isStarted = true;

        // Create an initial context and activate it
        ContextManager contextManager = getContextManager();
        Context mainContext = contextManager.createContext();
        contextManager.setContextActive(mainContext, true);

        // Load main script and call main function
        try {
            LuaScriptUtil.loadScript(mainContext, getScriptEnv().getScriptLoader(), KnownScriptFunctions.MAIN);
            LuaScriptUtil.callFunction(mainContext, mainFunctionName);
        } catch (Exception e) {
            LOG.warn("Error executing main function: \"{}\"", mainFunctionName, e);
        }
    }

    @Override
    public void restart() throws InitException {
        stop();

        start(KnownScriptFunctions.TITLESCREEN);
    }

    @Override
    public void stop() {
        if (!isStarted) {
            return;
        }
        isStarted = false;

        super.stop();
    }

    @Override
    public void update() {
        getScriptEnv().registerOnThread(); // Required after deserialization

        super.update();
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
        getContextManager().draw(drawBuffer);
    }

    protected LuaScriptEnv getScriptEnv() {
        return (LuaScriptEnv)getEnv().getScriptEnv();
    }

    @Override
    protected ContextManager getContextManager() {
        return (ContextManager)super.getContextManager();
    }

}
