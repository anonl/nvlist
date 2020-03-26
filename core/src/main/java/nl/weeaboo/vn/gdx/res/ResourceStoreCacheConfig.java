package nl.weeaboo.vn.gdx.res;

import nl.weeaboo.common.Checks;

/**
 * Settings for {@link ResourceStoreCache}.
 */
public final class ResourceStoreCacheConfig<T> {

    private IWeigher<T> weigher = new DefaultWeigher<>();
    private int maximumWeight = 20;

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
