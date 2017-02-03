package nl.weeaboo.vn.core;

import com.google.common.collect.Ordering;

public enum SkipMode {

    /** Not skipping. */
    NONE,

    /** Skip until the end of the paragraph. */
    PARAGRAPH,

    /** Skip until the end of the scene, or until a choice appears. */
    SCENE;

    private static final Ordering<SkipMode> ORDER = Ordering
            .explicit(NONE, PARAGRAPH, SCENE)
            .nullsFirst();

    /**
     * Returns the highest priority skip mode.
     */
    public static SkipMode max(SkipMode a, SkipMode b) {
        return ORDER.max(a, b);
    }

}
