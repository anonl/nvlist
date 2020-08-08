package nl.weeaboo.vn.desktop.debug;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
interface INvlistTaskRunner {

    CompletableFuture<Void> runOnNvlistThread(Runnable task);

    <T> CompletableFuture<T> supplyOnNvlistThread(Supplier<T> task);

}
