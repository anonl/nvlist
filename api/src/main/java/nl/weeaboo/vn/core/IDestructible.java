package nl.weeaboo.vn.core;

public interface IDestructible {

    /** Destroys the object, cleaning up any native resources. */
    void destroy();

    /**
     * @return {@code true} if this object is destroyed.
     * @see #destroy()
     */
    boolean isDestroyed();

}
