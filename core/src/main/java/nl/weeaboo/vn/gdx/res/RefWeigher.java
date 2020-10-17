package nl.weeaboo.vn.gdx.res;

/**
 * Weigher for values wrapped in a {@link Ref}.
 */
final class RefWeigher<T> implements IWeigher<Ref<T>> {

    private final IWeigher<T> delegate;

    RefWeigher(IWeigher<T> inner) {
        this.delegate = inner;
    }

    @Override
    public int weigh(Ref<T> resource) {
        T value = resource.get();
        if (value == null) {
            return 0;
        }
        return delegate.weigh(value);
    }

}
