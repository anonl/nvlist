package nl.weeaboo.vn.core.impl;

import nl.weeaboo.vn.core.IProgressListener;

public class TestProgressListener implements IProgressListener {

    private float lastProgress = Float.NaN;
    private int eventCount;

    @Override
    public synchronized void onProgressChanged(float progress) {
        lastProgress = progress;
        eventCount++;
    }

    public synchronized float getLastProgress() {
        return lastProgress;
    }

    public synchronized int consumeEventCount() {
        int result = eventCount;
        eventCount = 0;
        return result;
    }

}
