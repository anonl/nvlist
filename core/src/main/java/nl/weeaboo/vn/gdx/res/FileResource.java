package nl.weeaboo.vn.gdx.res;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.StaticRef;

final class FileResource<T> implements IResource<T> {

    private static final long serialVersionUID = 1L;

    private final StaticRef<? extends LoadingResourceStore<T>> store;
    private final FilePath filename;

    private transient Ref<T> valueRef;

    public FileResource(StaticRef<? extends LoadingResourceStore<T>> store, FilePath filename) {
        this.store = Checks.checkNotNull(store);
        this.filename = Checks.checkNotNull(filename);
    }

    @Override
    public @Nullable T get() {
        T value = getValue();
        if (value != null) {
            return value;
        }

        // Attempt to (re)load value
        set(store.get().getEntry(filename));
        return getValue();
    }

    private @Nullable T getValue() {
        Ref<T> ref = valueRef;
        return (ref != null ? ref.get() : null);
    }

    protected void set(Ref<T> ref) {
        this.valueRef = ref;
    }

}
