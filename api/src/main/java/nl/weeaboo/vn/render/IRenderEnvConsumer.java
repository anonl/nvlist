package nl.weeaboo.vn.render;

import nl.weeaboo.vn.signal.RenderEnvChangeSignal;

/**
 * Shared interface for classes that require a {@link IRenderEnv}.
 *
 * @deprecated Replaced by {@link RenderEnvChangeSignal}
 */
@Deprecated
public interface IRenderEnvConsumer {

    /**
     * Sets the rendering environment which contains information about the clipping/scaling performed by OpenGL.
     */
    void setRenderEnv(IRenderEnv env);

}
