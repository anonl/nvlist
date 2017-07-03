package nl.weeaboo.vn.buildtools.task;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractTask implements ITask {

    private final CopyOnWriteArrayList<IProgressListener> progressListeners = new CopyOnWriteArrayList<>();

    @Override
    public void cancel() {
    }

    @Override
    public final void addProgressListener(IProgressListener listener) {
        progressListeners.add(Objects.requireNonNull(listener));
    }

    @Override
    public final void removeProgressListener(IProgressListener listener) {
        progressListeners.remove(Objects.requireNonNull(listener));
    }

    protected final void fireProgress(String message) {
        progressListeners.forEach(ls -> ls.onProgress(message));
    }

    protected final void fireFinished(TaskResultType resultType, String message) {
        progressListeners.forEach(ls -> ls.onFinished(resultType, message));
    }

}
