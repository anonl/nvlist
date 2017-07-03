package nl.weeaboo.vn.buildgui;

import java.awt.Color;

import javax.swing.JComponent;

final class Styles {

    public static final Color WINDOW_BACKGROUND = new Color(0xe7e4e9);
    public static final Color WINDOW_BACKGROUND2 = new Color(0xe0e0e0);
    public static final Color HEADER_BACKGROUND = new Color(0xa38dae);
    public static final Color HEADER_BACKGROUND2 = new Color(0xBFA5CC);

    private Styles() {
    }

    public static void applyLargerFont(JComponent component) {
        component.setFont(component.getFont().deriveFont(14f));
    }

    public static void applyTitleFont(JComponent component) {
        component.setFont(component.getFont().deriveFont(20f));
    }

}
