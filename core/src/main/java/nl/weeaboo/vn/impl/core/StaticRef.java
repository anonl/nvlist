package nl.weeaboo.vn.impl.core;

import java.io.Serializable;
import java.util.UUID;

import nl.weeaboo.common.Checks;

public final class StaticRef<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final Class<T> type;

    public StaticRef(String id, Class<T> type) {
        this.id = Checks.checkNotNull(id);
        this.type = Checks.checkNotNull(type);
    }

    /** Creates a new randomly defined (and thus unsaveable) static reference. */
    public static <T> StaticRef<T> anonymous(Class<T> type) {
        return new StaticRef<>(UUID.randomUUID().toString(), type);
    }

    /**
     * Creates a new static reference with the given identifier and type.
     */
    public static <T> StaticRef<T> from(String id, Class<T> type) {
        return new StaticRef<>(id, type);
    }

    /**
     * Returns the value that this reference points to, never {@code null}.
     */
    public final T get() {
        return Checks.checkNotNull(getIfPresent(), "StaticRef." + id);
    }

    /**
     * Returns the value that this reference points to, or {@code null} if that value isn't set.
     */
    public final T getIfPresent() {
        StaticEnvironment instance = StaticEnvironment.getInstance();
        return instance.get(this);
    }

    /**
     * Sets the value that this reference points to.
     */
    public final void set(T value) {
        StaticEnvironment instance = StaticEnvironment.getInstance();
        instance.set(this, value);
    }

    String getId() {
        return id;
    }

    Class<T> getType() {
        return type;
    }

}