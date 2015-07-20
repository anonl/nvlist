package nl.weeaboo.gdx.res;

import nl.weeaboo.common.Checks;

/**
 * Returns a transformed view of a resource.
 *
 * @param <S> The original resource type.
 * @param <D> The transformed resource type.
 */
public abstract class TransformedResource<S, D> implements IResource<D> {

    private static final long serialVersionUID = 1L;

    private final IResource<S> inner;
    private transient D cachedTransformed;

    public TransformedResource(IResource<S> inner) {
        this.inner = Checks.checkNotNull(inner);
    }

    @Override
    public final D get() {
        D result = cachedTransformed;
        if (result == null) {
            S original = inner.get();
            if (original != null) {
                result = transform(original);
                cachedTransformed = result;
            }
        }
        return result;
    }

    /**
     * @param original The original resource, will never be {@code null}.
     */
    protected abstract D transform(S original);

}
