package nl.weeaboo.vn.gdx.res;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.gdx.res.IResource;
import nl.weeaboo.vn.impl.core.StaticRef;

/** Resource stub backed by a static reference */
public class StaticResourceStub<T> implements IResource<T> {

    private static final long serialVersionUID = 1L;

    private final StaticRef<T> ref;

    public StaticResourceStub(StaticRef<T> ref) {
        this.ref = Checks.checkNotNull(ref);
    }

    @Override
    public T get() {
        return ref.getIfPresent();
    }

}
