package nl.weeaboo.vn.gdx.res;

import com.google.common.cache.Weigher;

/**
 * Estimates relative cache weight for a resource.
 */
public interface IWeigher<T> {

    /**
     * Returns the cache 'weight' for the given object.
     *
     * @param object The object to weigh.
     * @see Weigher#weigh(Object, Object)
     */
    int weigh(T object);

}