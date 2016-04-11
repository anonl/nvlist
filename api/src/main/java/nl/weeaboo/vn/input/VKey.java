package nl.weeaboo.vn.input;

import java.io.Serializable;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;

/** Virtual key code. Used by the key config for mapping logical actions to one or more inputs. */
public final class VKey implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final VKey UP = new VKey("up");
    public static final VKey DOWN = new VKey("down");
    public static final VKey LEFT = new VKey("left");
    public static final VKey RIGHT = new VKey("right");

    public static final VKey CONFIRM = new VKey("confirm");
    public static final VKey CANCEL = new VKey("cancel");
    public static final VKey TEXT_CONTINUE = new VKey("textContinue");
    public static final VKey SKIP = new VKey("skip");

    public static final VKey MOUSE_LEFT = new VKey("mouseLeft");

    private static final ImmutableList<VKey> STANDARD_KEYS = ImmutableList.of(
        UP, DOWN, LEFT, RIGHT,
        CONFIRM, CANCEL, TEXT_CONTINUE, SKIP,
        MOUSE_LEFT
    );

    private final String id;

    private VKey(String id) {
        this.id = Checks.checkNotNull(id);
    }

    public static VKey fromString(String id) {
        return new VKey(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VKey)) {
            return false;
        }

        VKey other = (VKey)obj;
        return id.equals(other.id);
    }

    @Override
    public String toString() {
        return getId();
    }

    public static Iterable<VKey> getStandardKeys() {
        return STANDARD_KEYS;
    }
}
