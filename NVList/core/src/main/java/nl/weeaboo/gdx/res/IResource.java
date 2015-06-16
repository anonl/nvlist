package nl.weeaboo.gdx.res;

public interface IResource<T> {

    /**
     * Returns a reference to the resource, or {@code null} if the resource could not be loaded.
     */
    T get();
    
}
