package nl.weeaboo.vn.gdx.res;

import java.lang.ref.WeakReference;

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
    private transient WeakReference<S> cachedOriginal;
    private transient D cachedTransformed;

    public TransformedResource(IResource<S> inner) {
        this.inner = Checks.checkNotNull(inner);
    }

    @Override
    public final D get() {
        S original = inner.get();
        D result = cachedTransformed;

        if (result != null) {
            S cachedOriginalValue = (cachedOriginal != null ? cachedOriginal.get() : null);
            if (original == cachedOriginalValue) {
                return cachedTransformed;
            }
        }

        if (original != null) {
            cachedOriginal = new WeakReference<>(original);
            result = transform(original);
            if (result != null) {
                cachedTransformed = result;
            }
        }
        return result;
    }

    /**
     * @param original The original resource, will never be {@code null}.
     * @return The transformed resource, or {@code null} if the resource couldn't be transformed.
     */
    protected abstract D transform(S original);

}
