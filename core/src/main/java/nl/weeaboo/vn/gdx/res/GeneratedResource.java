package nl.weeaboo.vn.gdx.res;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.impl.core.StaticRef;

@CustomSerializable
final class GeneratedResource<T extends Serializable & Disposable> implements IResource<T> {

    private static final long serialVersionUID = 1L;

    private final StaticRef<? extends GeneratedResourceStore> store;
    private final T value;

    GeneratedResource(StaticRef<? extends GeneratedResourceStore> store, T val) {
        this.store = Checks.checkNotNull(store);
        this.value = Checks.checkNotNull(val);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        store.get().register(value);
    }

    @Override
    public T get() {
        return value;
    }

}
