package nl.weeaboo.vn.render;

import nl.weeaboo.vn.image.ITexture;

public interface IOffscreenRenderTask extends IAsyncRenderTask {

    /**
     * Execute the task.
     */
    void render();

    /**
     * @return If available, the {@link ITexture} result, or else {@code null}.
     */
    ITexture getResult();

}
