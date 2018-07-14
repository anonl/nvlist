package nl.weeaboo.vn.gdx.res;

import javax.annotation.Nullable;

public final class Ref<T> {

    private @Nullable T referent;

    public Ref(@Nullable T referent) {
        this.referent = referent;
    }

    public @Nullable T get() {
        return referent;
    }

    public void invalidate() {
        referent = null;
    }

}
