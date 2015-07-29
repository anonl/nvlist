package nl.weeaboo.styledtext;

public enum EFontStyle {

    PLAIN, ITALIC, BOLD, BOLD_ITALIC;

    public boolean isBold() {
        return this == BOLD || this == BOLD_ITALIC;
    }

    public boolean isItalic() {
        return this == ITALIC || this == BOLD_ITALIC;
    }

    public static EFontStyle combine(EFontStyle a, EFontStyle b) {
        if (a == null) return b;
        if (b == null) return a;

        switch (a) {
        case ITALIC:
            return (b.isBold() ? BOLD_ITALIC : a);
        case BOLD:
            return (b.isItalic() ? BOLD_ITALIC : a);
        default:
            return a;
        }
    }

    public static EFontStyle fromString(String str) {
        for (EFontStyle style : values()) {
            if (style.toString().equalsIgnoreCase(str)) {
                return style;
            }
        }
        return null;
    }

}
