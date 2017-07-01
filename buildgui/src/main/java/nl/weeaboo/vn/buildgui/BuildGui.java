package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.buildgui.task.ITaskController;

@SuppressWarnings("serial")
final class BuildGui extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(BuildGui.class);

    public BuildGui(IBuildGuiController controller) {
        setTitle("NVList Build Tool");
        setDefaultWindowIcon(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(StyleConstants.WINDOW_BACKGROUND);

        HeaderPanel headerPanel = new HeaderPanel();
        JPanel bottomPanel = createBottomPanel(controller);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(1024, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createBottomPanel(IBuildGuiController controller) {
        ITaskController taskController = controller.getTaskController();

        ProgressPanel progressPanel = new ProgressPanel();
        taskController.addActiveTaskListener(progressPanel);

        TaskButton taskButton = new TaskButton(controller.getBuildController());
        taskController.addActiveTaskListener(taskButton);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(progressPanel, BorderLayout.CENTER);
        panel.add(taskButton, BorderLayout.EAST);
        return panel;
    }

    private static void setDefaultWindowIcon(Window window) {
        try {
            BufferedImage icon = ImageIO.read(BuildGui.class.getResource("icon.png"));
            window.setIconImages(SwingImageUtil.getScaledImages(icon, 32, 64, 256));
        } catch (IOException e) {
            LOG.warn("Error loading window icon", e);
        }
    }

}
