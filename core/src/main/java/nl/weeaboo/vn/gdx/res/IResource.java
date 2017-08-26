package nl.weeaboo.vn.gdx.res;

import java.io.Serializable;

import javax.annotation.Nullable;

/**
 * Provides an indirect reference to a disposable resource. This object handles any necessary (re)loading if
 * the resource isn't currently loaded.
 */
public interface IResource<T> extends Serializable {

    /**
     * Returns a reference to the resource, or {@code null} if the resource could not be loaded.
     */
    @Nullable T get();

}
