package nl.weeaboo.vn.buildtools.optimizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Represents a thread pool capable of running tasks on multiple processor cores.
 */
public interface IParallelExecutor {

    /**
     * Runs the given tasks in parallel. Blocks until all tasks have finished.
     *
     * @return A {@link Future} per submitted task that can be used to query the task's result.
     *
     * @throws InterruptedException If the current thread is interrupted while still waiting for one or more
     *         tasks to finish.
     */
    <T> List<Future<T>> invokeAndWait(Collection<Callable<T>> tasks) throws InterruptedException;

    /**
     * Convenience method for applying a fixed function on a sequence of input values.
     *
     * @throws InterruptedException If the current thread is interrupted while still waiting for one or more
     *         tasks to finish.
     */
    default <T> List<Future<Void>> invokeAndWait(Iterable<T> inputs, Consumer<T> inputProcessingTask)
            throws InterruptedException {

        List<Callable<Void>> tasks = new ArrayList<>();
        for (T input : inputs) {
            tasks.add(() -> {
                inputProcessingTask.accept(input);
                return null;
            });
        }
        return invokeAndWait(tasks);
    }

}
