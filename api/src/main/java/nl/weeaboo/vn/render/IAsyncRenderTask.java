package nl.weeaboo.vn.render;

import java.io.Serializable;

public interface IAsyncRenderTask extends Serializable {

    /**
     * Requests cancellation of the asynchronous render operation.
     */
    void cancel();

    /**
     * @return {@code true} if the render operation failed or was cancelled.
     */
    boolean isFailed();

    /**
     * @return {@code true} when the render operation has completed successfully.
     */
    boolean isAvailable();

    /**
     * @return {@code true} if the render result is omitted when saving. If {@code false}, the render result
     *         (if available) will be stored as part of save files.
     */
    boolean isTransient();

}
