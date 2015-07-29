package nl.weeaboo.styledtext;

import java.util.Locale;

public enum ETextAlign {

    NORMAL, REVERSE, LEFT, CENTER, RIGHT;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

	public static ETextAlign fromString(String str) {
		for (ETextAlign a : values()) {
			if (a.toString().equals(str)) {
				return a;
			}
		}
		return null;
	}

    /**
     * @return {@code -1} if left-aligned, {@code 0} if center-aligned, {@code 1} if right-aligned.
     */
    public int getHorizontalAlign(boolean isRightToLeft) {
        int defaultDir = (isRightToLeft ? 1 : -1);

        switch (this) {
        case NORMAL:
            return defaultDir;
        case REVERSE:
            return -defaultDir;
        case LEFT:
            return -1;
        case CENTER:
            return 0;
        case RIGHT:
            return 1;
        default:
            return defaultDir;
        }
    }

}
