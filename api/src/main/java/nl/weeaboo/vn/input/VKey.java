package nl.weeaboo.vn.input;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.AbstractId;

/** Virtual key code. Used by the key config for mapping logical actions to one or more inputs. */
public final class VKey extends AbstractId {

    private static final long serialVersionUID = 1L;

    public static final VKey UP = new VKey("up");
    public static final VKey DOWN = new VKey("down");
    public static final VKey LEFT = new VKey("left");
    public static final VKey RIGHT = new VKey("right");

    public static final VKey CONFIRM = new VKey("confirm");
    public static final VKey CANCEL = new VKey("cancel");
    public static final VKey TEXT_CONTINUE = new VKey("textContinue");
    public static final VKey SHOW_TEXT_LOG = new VKey("showTextLog");
    public static final VKey SKIP = new VKey("skip");
    public static final VKey ALT_SKIP = new VKey("altSkip");
    public static final VKey SCENE_SKIP = new VKey("sceneSkip");

    public static final VKey MOUSE_LEFT = new VKey("mouseLeft");

    // Special keys to access debug/test functionality
    public static final VKey TOGGLE_CONSOLE = new VKey("toggleConsole");
    public static final VKey TOGGLE_OSD = new VKey("toggleOsd");

    private static final ImmutableList<VKey> STANDARD_KEYS = ImmutableList.of(
            UP, DOWN, LEFT, RIGHT,
            CONFIRM, CANCEL, TEXT_CONTINUE, SHOW_TEXT_LOG,
            SKIP, ALT_SKIP, SCENE_SKIP,
            MOUSE_LEFT
    );

    private VKey(String id) {
        super(id);
    }

    /** Creates a {@link VKey} instance from an ID string. */
    public static VKey fromString(String id) {
        return new VKey(id);
    }

    /** Returns the default set of {@link VKey} instances. */
    public static Iterable<VKey> getStandardKeys() {
        return STANDARD_KEYS;
    }
}
