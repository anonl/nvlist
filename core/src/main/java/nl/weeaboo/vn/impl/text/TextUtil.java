package nl.weeaboo.vn.impl.text;

import com.google.common.base.Strings;

import nl.weeaboo.styledtext.StyledText;

public final class TextUtil {

    private TextUtil() {
    }

    /**
     * Null-safe conversion from String to {@link StyledText}.
     */
    public static StyledText toStyledText(String str) {
        return new StyledText(Strings.nullToEmpty(str));
    }

}
