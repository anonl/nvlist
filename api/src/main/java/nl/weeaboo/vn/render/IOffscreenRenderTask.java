package nl.weeaboo.vn.render;

import javax.annotation.Nullable;

import nl.weeaboo.vn.image.ITexture;

public interface IOffscreenRenderTask extends IAsyncRenderTask {

    /**
     * Execute the task.
     */
    void render();

    /**
     * @return If available, the {@link ITexture} result, or else {@code null}.
     */
    @Nullable ITexture getResult();

}
