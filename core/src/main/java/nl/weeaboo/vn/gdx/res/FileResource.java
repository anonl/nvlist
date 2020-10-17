package nl.weeaboo.vn.gdx.res;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.StaticRef;

/**
 * (Re)loadable file-based resource.
 */
final class FileResource<T> extends AbstractResource<T> {

    private static final long serialVersionUID = 1L;

    private final StaticRef<? extends LoadingResourceStore<T>> store;
    private final FilePath filename;

    private @Nullable Ref<T> ref;

    FileResource(StaticRef<? extends LoadingResourceStore<T>> store, FilePath filename, @Nullable Ref<T> ref) {
        this.store = Checks.checkNotNull(store);
        this.filename = Checks.checkNotNull(filename);
        this.ref = ref;
    }

    @Override
    public @Nullable T get() {
        if (ref == null || ref.get() == null) {
            // Attempt to (re)load value
            ref = store.get().getValueRef(filename);
        }

        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    void invalidate() {
        ref = null;
    }

    @Override
    public String toString() {
        return "<" + filename + ">";
    }

}
