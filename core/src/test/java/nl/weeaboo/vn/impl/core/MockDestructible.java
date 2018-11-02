package nl.weeaboo.vn.impl.core;

import java.io.Serializable;
import java.util.Locale;

import nl.weeaboo.vn.core.IDestructible;

final class MockDestructible implements IDestructible, Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private boolean destroyed;

    MockDestructible(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MockDestructible)) {
            return false;
        }
        MockDestructible dummy = (MockDestructible)obj;
        return id == dummy.id;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s[id=%s]",
                getClass().getSimpleName(), id);
    }

}