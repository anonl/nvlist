package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

@SuppressWarnings("serial")
final class HeaderPanel extends JPanel implements IProjectModelListener {

    private static final int BORDER_SIZE = 10;

    private final ImageIcon logoIcon;
    private final ProjectFolderConfigPanel folderConfigPanel;

    public HeaderPanel(IBuildGuiController guiController) {
        logoIcon = new ImageIcon(getClass().getResource("logo.png"));

        folderConfigPanel = new ProjectFolderConfigPanel(guiController);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(folderConfigPanel);

        setOpaque(true);
        setBackground(Styles.HEADER_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        setPreferredSize(new Dimension(logoIcon.getIconWidth() + 2 * BORDER_SIZE,
                logoIcon.getIconHeight() + 2 * BORDER_SIZE));
        setLayout(new BorderLayout());
        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        logoIcon.paintIcon(this, graphics, BORDER_SIZE, BORDER_SIZE);
    }

    @Override
    public void onProjectChanged(NvlistProjectConnection projectModel) {
        folderConfigPanel.onProjectChanged(projectModel);
    }

}
