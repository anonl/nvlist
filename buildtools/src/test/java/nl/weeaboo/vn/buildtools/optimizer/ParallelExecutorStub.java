package nl.weeaboo.vn.buildtools.optimizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.Futures;

public final class ParallelExecutorStub implements IParallelExecutor {

    @Override
    public <T> List<Future<T>> invokeAndWait(Collection<Callable<T>> tasks) throws InterruptedException {
        List<Future<T>> futures = new ArrayList<>();
        for (Callable<T> task : tasks) {
            try {
                T result = task.call();
                futures.add(Futures.immediateFuture(result));
            } catch (Exception e) {
                futures.add(Futures.immediateFailedFuture(e));
            }
        }
        return futures;
    }

}
