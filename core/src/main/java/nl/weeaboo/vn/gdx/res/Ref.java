package nl.weeaboo.vn.gdx.res;

import javax.annotation.Nullable;

/**
 * Invalidatable reference.
 */
// Unlike IResource, this class isn't serializable.
final class Ref<T> {

    private @Nullable T referent;

    Ref(@Nullable T referent) {
        this.referent = referent;
    }

    public @Nullable T get() {
        return referent;
    }

    public void invalidate() {
        referent = null;
    }

}