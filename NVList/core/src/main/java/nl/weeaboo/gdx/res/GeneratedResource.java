package nl.weeaboo.gdx.res;

import nl.weeaboo.common.Checks;

final class GeneratedResource<T> implements IResource<T> {

    private final T value;
    
    public GeneratedResource(T val) {
        this.value = Checks.checkNotNull(val);
    }
    
    @Override
    public T get() {
        return value;        
    }

}
