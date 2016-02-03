package nl.weeaboo.gdx.res;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.impl.StaticRef;

final class FileResource<T> implements IResource<T> {

    private static final long serialVersionUID = 1L;

    private final StaticRef<? extends LoadingResourceStore<T>> store;
    private final String filename;

    private transient Ref<T> valueRef;

    public FileResource(StaticRef<? extends LoadingResourceStore<T>> store, String filename) {
        this.store = Checks.checkNotNull(store);
        this.filename = Checks.checkNotNull(filename);
    }

    @Override
    public T get() {
        T value = getValue();
        if (value != null) {
            return value;
        }

        // Attempt to (re)load value
        set(store.get().getEntry(filename));
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
