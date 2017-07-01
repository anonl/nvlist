package nl.weeaboo.vn.buildgui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
final class HeaderPanel extends JPanel {

    private static final int BORDER_SIZE = 10;

    private final ImageIcon logoIcon;

    public HeaderPanel() {
        logoIcon = new ImageIcon(getClass().getResource("logo.png"));

        setOpaque(true);
        setBackground(StyleConstants.HEADER_BACKGROUND);
        setPreferredSize(new Dimension(logoIcon.getIconWidth() + 2 * BORDER_SIZE,
                logoIcon.getIconHeight() + 2 * BORDER_SIZE));
        setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        logoIcon.paintIcon(this, graphics, BORDER_SIZE, BORDER_SIZE);
    }

}
