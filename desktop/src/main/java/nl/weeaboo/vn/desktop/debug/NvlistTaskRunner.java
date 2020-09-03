package nl.weeaboo.vn.desktop.debug;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
final class NvlistTaskRunner implements INvlistTaskRunner {

    private final Executor executor;

    NvlistTaskRunner(Executor executor) {
        this.executor = Objects.requireNonNull(executor);
    }

    @Override
    public CompletableFuture<Void> runOnNvlistThread(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }

    @Override
    public <T> CompletableFuture<T> supplyOnNvlistThread(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor);
    }

}
