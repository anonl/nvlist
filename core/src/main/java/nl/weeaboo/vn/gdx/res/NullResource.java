package nl.weeaboo.vn.gdx.res;

import javax.annotation.Nullable;

/**
 * Resource reference which always returns {@code null}.
 */
public final class NullResource<T> extends AbstractResource<T> {

    private static final long serialVersionUID = 1L;

    private NullResource() {
    }

    public static <T> IResource<T> getInstance() {
        return new NullResource<>();
    }

    @Override
    public @Nullable T get() {
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
