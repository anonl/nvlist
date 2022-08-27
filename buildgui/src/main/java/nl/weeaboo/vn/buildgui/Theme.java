package nl.weeaboo.vn.buildgui;

import java.awt.Color;

/**
 * UI color theme
 */
public class Theme {

    public Color windowBackground = new Color(0xe7e4e9);
    public Color windowBackground2 = new Color(0xe0e0e0);
    public Color headerBackground = new Color(0xa38dae);
    public Color headerBackground2 = new Color(0xBFA5CC);
    public Color glassBackground = new Color(0x80e0e0e0, true);

    public final LogStyles logStyles = new LogStyles();

    public static final class LogStyles {
        public Color errorColor = Color.RED.darker();
        public Color warningColor = Color.ORANGE;
        public Color debugColor = Color.GRAY;
        public Color infoColor = Color.BLACK;

        public Color gradleCompleteColor = Color.GREEN.darker();
        public Color gradleFailedColor = errorColor;
    }

    public static final class LightTheme extends Theme {

    }

    public static final class DarkTheme extends Theme {
        public DarkTheme() {
            windowBackground = new Color(0x5b595c);
            windowBackground2 = new Color(0x49474a);
            headerBackground = new Color(0x85728f);
            headerBackground2 = new Color(0x5d5263);
            glassBackground = new Color(0xc03c3b3d, true);

            logStyles.infoColor = Color.WHITE;
        }
    }
}
