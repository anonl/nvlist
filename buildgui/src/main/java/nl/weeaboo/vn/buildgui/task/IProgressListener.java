package nl.weeaboo.vn.buildgui.task;

public interface IProgressListener {

    default void onProgress(String message) {
    }

    default void onFinished() {
    }

}
