package nl.weeaboo.vn.render;

/**
 * Shared interface for classes that require a {@link IRenderEnv}.
 */
public interface IRenderEnvConsumer {

    /**
     * Sets the rendering environment which contains information about the clipping/scaling performed by OpenGL.
     */
    void setRenderEnv(IRenderEnv env);

}
