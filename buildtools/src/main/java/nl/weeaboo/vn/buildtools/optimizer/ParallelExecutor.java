package nl.weeaboo.vn.buildtools.optimizer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

final class ParallelExecutor implements IParallelExecutor {

    private final ExecutorService executor;

    public ParallelExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public <T> List<Future<T>> invokeAndWait(Collection<Callable<T>> tasks) throws InterruptedException {
        return executor.invokeAll(tasks);
    }

    static ExecutorService newExecutorService() {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("parallel-%d").build();
        return Executors.newFixedThreadPool(numProcessors, threadFactory);
    }

}
