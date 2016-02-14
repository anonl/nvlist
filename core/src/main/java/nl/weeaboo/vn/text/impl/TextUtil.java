package nl.weeaboo.vn.text.impl;

import com.google.common.base.Strings;

import nl.weeaboo.styledtext.StyledText;

public final class TextUtil {

    private TextUtil() {
    }

    public static StyledText toStyledText(String str) {
        return new StyledText(Strings.nullToEmpty(str));
    }

}
