package nl.weeaboo.vn.gdx.res;

/**
 * Weigher for values wrapped in a {@link IResource}.
 */
final class ResourceWeigher<T> implements IWeigher<IResource<T>> {

    private final IWeigher<T> delegate;

    ResourceWeigher(IWeigher<T> inner) {
        this.delegate = inner;
    }

    @Override
    public int weigh(IResource<T> resource) {
        T value = resource.get();
        if (value == null) {
            return 0;
        }
        return delegate.weigh(value);
    }

}