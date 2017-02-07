package nl.weeaboo.vn.impl.core;

import nl.weeaboo.vn.core.IProgressListener;

public class TestProgressListener implements IProgressListener {

    private float lastProgress = Float.NaN;
    private int eventCount;

    @Override
    public synchronized void onProgressChanged(float progress) {
        lastProgress = progress;
        eventCount++;
    }

    /**
     * @return The most recently received progress amount, or {@code Float#NaN} if no progress was ever received.
     */
    public synchronized float getLastProgress() {
        return lastProgress;
    }

    /**
     * Returns the value of an internal counter that counts the number of times that a progress changed event was
     * received. The internal counter is then reset to {@code 0}.
     */
    public synchronized int consumeEventCount() {
        int result = eventCount;
        eventCount = 0;
        return result;
    }

}
