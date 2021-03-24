package nl.weeaboo.vn.buildtools.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

public final class ProgressListenerMock implements IProgressListener {

    private final List<String> progressMessages = new ArrayList<>();
    private @Nullable TaskResultType result;

    @Override
    public void onProgress(String message) {
        progressMessages.add(message);
    }

    public List<String> consumeProgress() {
        List<String> result = ImmutableList.copyOf(progressMessages);
        progressMessages.clear();
        return result;
    }

    @Override
    public void onFinished(TaskResultType resultType, String message) {
        this.result = resultType;
        progressMessages.add(message);
    }

    public @Nullable TaskResultType getResult() {
        return result;
    }
}
