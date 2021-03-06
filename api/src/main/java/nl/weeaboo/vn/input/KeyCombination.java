package nl.weeaboo.vn.input;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Key combination of zero or more {@link KeyCode}.
 */
public final class KeyCombination {

    private final ImmutableSet<KeyCode> keys;

    public KeyCombination(Collection<KeyCode> keys) {
        this.keys = Sets.immutableEnumSet(keys);
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KeyCombination)) {
            return false;
        }

        KeyCombination other = (KeyCombination)obj;
        return keys.equals(other.keys);
    }

    /**
     * @return A read-only collection of the keys that must simultaneously be held to activate this key combination.
     */
    public Collection<KeyCode> getKeys() {
        return keys;
    }

    @Override
    public String toString() {
        return keys.toString();
    }

}
