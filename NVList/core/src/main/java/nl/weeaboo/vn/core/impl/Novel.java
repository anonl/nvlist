package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.lua.LuaScriptEnv;

public class Novel extends AbstractNovel {

    // --- Note: This class uses manual serialization ---
    private transient boolean isStarted;
    // --- Note: This class uses manual serialization ---

    public Novel(DefaultEnvironment env) {
        super(env);

        Checks.checkArgument(env.getContextManager() instanceof ContextManager,
                "Unexpected ContextManager type");
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
    public void start(String mainFunctionName) {
        isStarted = true;

        super.start(mainFunctionName);
    }

    @Override
    public void stop() {
        if (!isStarted) {
            return;
        }
        isStarted = false;

        ISaveModule saveModule = getSaveModule();
        saveModule.savePersistent();
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
