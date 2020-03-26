package nl.weeaboo.vn.core;

/**
 * Object which can destroy (dispose, close) itself.
 */
public interface IDestructible {

    /** Destroys the object, cleaning up any native resources. */
    void destroy();

    /**
     * @return {@code true} if this object is destroyed.
     * @see #destroy()
     */
    boolean isDestroyed();

}
