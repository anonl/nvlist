package nl.weeaboo.vn.impl.script.lua;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nl.weeaboo.common.Checks;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.vn.impl.script.DefaultScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptEnv;
import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptLoader;
import nl.weeaboo.vn.script.ScriptException;

/**
 * Default implementation of {@link IScriptEnv}.
 */
@CustomSerializable
public class LuaScriptEnv implements IScriptEnv {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    private final LuaRunState runState;
    private final LuaScriptLoader loader;
    private final List<ILuaScriptEnvInitializer> initializers = new ArrayList<>();
    private IScriptExceptionHandler exceptionHandler = DefaultScriptExceptionHandler.INSTANCE;

    private boolean initialized;

    public LuaScriptEnv(LuaRunState runState, LuaScriptLoader loader) {
        this.runState = runState;
        this.loader = loader;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        registerOnThread();
    }

    /**
     * Run the script environment initializers.
     * @throws ScriptException If script initialization fails.
     */
    public void initEnv() throws ScriptException {
        initialized = true;

        registerOnThread();
        loader.initEnv();
        LuaTypeCoercions.install(runState);

        for (ILuaScriptEnvInitializer init : initializers) {
            init.initEnv(this);
        }
    }

    /**
     * Registers this script env as the 'current' script env if the thread on which this method is called.
     */
    public final void registerOnThread() {
        runState.registerOnThread();
    }

    /**
     * Adds an additional script environment initialization function.
     *
     * @throws IllegalStateException If the script environment has already been initialized.
     */
    public void addInitializer(ILuaScriptEnvInitializer init) {
        Checks.checkState(!initialized, "Can't change initializers after initEnv() has been called.");

        initializers.add(init);
    }

    /**
     * @return The internal {@link LuaRunState} used by this script environment.
     */
    public LuaRunState getRunState() {
        return runState;
    }

    /**
     * @return The Lua environment's globals table.
     */
    public LuaTable getGlobals() {
        return runState.getGlobalEnvironment();
    }

    @Override
    public IScriptLoader getScriptLoader() {
        return loader;
    }

    public IScriptExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public void setExceptionHandler(IScriptExceptionHandler exceptionHandler) {
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
    }

}
