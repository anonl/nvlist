package nl.weeaboo.vn.render;

import nl.weeaboo.vn.image.ITexture;

public interface IOffscreenRenderTask extends IAsyncRenderTask {

    void render();

    /**
     * @return If available, the {@link ITexture} result, or else {@code null}.
     */
    ITexture getResult();

}
