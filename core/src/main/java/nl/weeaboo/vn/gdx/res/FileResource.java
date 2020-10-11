package nl.weeaboo.vn.gdx.res;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.StaticRef;

/**
 * (Re)loadable resource.
 */
final class FileResource<T> extends AbstractResource<T> {

    private static final long serialVersionUID = 1L;

    private final StaticRef<? extends LoadingResourceStore<T>> store;
    private final FilePath filename;

    private transient @Nullable T value;

    public FileResource(StaticRef<? extends LoadingResourceStore<T>> store, FilePath filename, T value) {
        this.store = Checks.checkNotNull(store);
        this.filename = Checks.checkNotNull(filename);
        this.value = value;
    }

    @Override
    public @Nullable T get() {
        T result = value;
        if (result == null) {
            // Attempt to (re)load value
            result = store.get().loadResource(filename);
            value = result;
        }
        return result;
    }

    void invalidate() {
        value = null;
    }

    @Override
    public String toString() {
        return "<" + filename + ">";
    }

}
