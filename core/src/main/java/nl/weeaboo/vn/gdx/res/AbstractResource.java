package nl.weeaboo.vn.gdx.res;

/**
 * Base implementation of {@link IResource}.
 */
public abstract class AbstractResource<T> implements IResource<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }

}
