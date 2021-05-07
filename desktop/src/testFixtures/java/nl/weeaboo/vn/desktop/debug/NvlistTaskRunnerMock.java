package nl.weeaboo.vn.desktop.debug;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;

final class NvlistTaskRunnerMock implements INvlistTaskRunner {

    private final LuaScriptEnv scriptEnv;

    public NvlistTaskRunnerMock(LuaScriptEnv scriptEnv) {
        this.scriptEnv = Objects.requireNonNull(scriptEnv);
    }

    @Override
    public CompletableFuture<Void> runOnNvlistThread(Runnable task) {
        return supplyOnNvlistThread(() -> {
            task.run();
            return null;
        });
    }

    @Override
    public <T> CompletableFuture<T> supplyOnNvlistThread(Supplier<T> task) {
        scriptEnv.registerOnThread();

        return CompletableFuture.completedFuture(task.get());
    }

}
