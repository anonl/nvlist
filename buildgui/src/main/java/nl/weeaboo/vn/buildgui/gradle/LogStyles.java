package nl.weeaboo.vn.buildgui.gradle;

import java.awt.Color;

final class LogStyles {

    public static final Color ERROR_COLOR = Color.RED.darker();
    public static final Color WARNING_COLOR = Color.ORANGE;
    public static final Color DEBUG_COLOR = Color.GRAY;
    public static final Color INFO_COLOR = Color.BLACK;

    public static final Color GRADLE_COMPLETE_COLOR = Color.GREEN.darker();
    public static final Color GRADLE_FAILED_COLOR = ERROR_COLOR;

}
