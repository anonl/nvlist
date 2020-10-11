package nl.weeaboo.vn.gdx.res;

import com.google.common.base.Function;

import nl.weeaboo.common.Checks;

/**
 * Settings for {@link ResourceStoreCache}.
 */
public final class ResourceStoreCacheConfig<T> {

    private IWeigher<T> weigher = new DefaultWeigher<>();
    private int maximumWeight = 20;

    /**
     * Returns an equivalent config for a different resource type.
     */
    <D> ResourceStoreCacheConfig<D> map(Function<IWeigher<T>, IWeigher<D>> weigherMapper) {
        ResourceStoreCacheConfig<D> result = new ResourceStoreCacheConfig<>();
        result.setWeigher(weigherMapper.apply(weigher));
        result.setMaximumWeight(maximumWeight);
        return result;
    }

    /**
     * The weigher assigns a relative size to cached entries.
     */
    public IWeigher<T> getWeigher() {
        return weigher;
    }

    /**
     * @see #getWeigher()
     */
    public void setWeigher(IWeigher<T> weigher) {
        this.weigher = Checks.checkNotNull(weigher);
    }

    /**
     * The maximum cache size (in 'weight').
     *
     * @see #getWeigher()
     */
    public int getMaximumWeight() {
        return maximumWeight;
    }

    /**
     * @see #getMaximumWeight()
     */
    public void setMaximumWeight(int maximumWeight) {
        this.maximumWeight = maximumWeight;
    }

}
