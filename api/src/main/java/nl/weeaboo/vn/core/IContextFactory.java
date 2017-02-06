package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.vn.render.IRenderEnv;

public interface IContextFactory<C extends IContext> extends Serializable {

    /**
     * Creates a new context.
     */
    C newContext();

    /**
     * Sets the rendering environment which contains information about the clipping/scaling performed by OpenGL.
     */
    void setRenderEnv(IRenderEnv env);

}
