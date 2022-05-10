package nl.weeaboo.vn.buildgui;

import javax.swing.JComponent;

public final class Styles {

    public static Theme theme = new Theme.DarkTheme();

    private Styles() {
    }

    public static void applyLargerFont(JComponent component) {
        component.setFont(component.getFont().deriveFont(14f));
    }

    public static void applyTitleFont(JComponent component) {
        component.setFont(component.getFont().deriveFont(20f));
    }

}
