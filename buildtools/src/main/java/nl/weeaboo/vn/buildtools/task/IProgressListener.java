package nl.weeaboo.vn.buildtools.task;

public interface IProgressListener {

    /**
     * @param message Human readable progress details message.
     */
    default void onProgress(String message) {
    }

    /**
     * Called when the task finishes -- even if it finishes with an error.
     */
    default void onFinished() {
    }

}
