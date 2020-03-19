package nl.weeaboo.vn.impl.text;

import com.google.common.base.Strings;

import nl.weeaboo.styledtext.StyledText;

/**
 * Utility functions related to text.
 */
public final class TextUtil {

    public static final String DEFAULT_FONT_NAME = "default";

    private TextUtil() {
    }

    /**
     * Null-safe conversion from String to {@link StyledText}.
     */
    public static StyledText toStyledText(String str) {
        return new StyledText(Strings.nullToEmpty(str));
    }

}
