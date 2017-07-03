package nl.weeaboo.vn.buildtools.task;

public interface IProgressListener {

    /**
     * @param message Human readable progress details message.
     */
    default void onProgress(String message) {
    }

    /**
     * Called when the task finishes -- even if it finishes with an error.
     * @param resultType Indicates success or failure.
     * @param message Human readable result message.
     */
    default void onFinished(TaskResultType resultType, String message) {
    }

}
