package nl.weeaboo.gdx.res;

import nl.weeaboo.common.Checks;

final class CachedResource<T> implements IResource<T> {

    private final LoadingResourceStore<T> cache;
    private final String filename;
    
    private Ref<T> valueRef;
    
    public CachedResource(LoadingResourceStore<T> cache, String filename) {
        this.cache = Checks.checkNotNull(cache);
        this.filename = Checks.checkNotNull(filename);
    }
    
    @Override
    public T get() {
        T value = getValue();        
        if (value != null) {
            return value;
        }
        
        // Attempt to (re)load value        
        set(cache.getEntry(filename));
        return getValue();
    }
    
    private T getValue() {
        Ref<T> ref = valueRef;
        return (ref != null ? ref.get() : null);
    }
    
    protected void set(Ref<T> ref) {
        this.valueRef = ref;
    }

}
