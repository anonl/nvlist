package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.buildgui.task.ITaskController;

@SuppressWarnings("serial")
final class BuildGui extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(BuildGui.class);

    private final IBuildGuiController controller;

    public BuildGui(IBuildGuiController controller) {
        this.controller = Objects.requireNonNull(controller);

        setTitle("NVList Build Tool");
        setDefaultWindowIcon(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(Styles.WINDOW_BACKGROUND);
        setLayout(new BorderLayout());
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        setPreferredSize(new Dimension(1024, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        BuildGuiModel model = controller.getModel();

        HeaderPanel headerPanel = new HeaderPanel(controller);
        model.addProjectListener(headerPanel);
        return headerPanel;
    }

    private JPanel createCenterPanel() {
        BuildGuiModel model = controller.getModel();
        IBuildController buildController = controller.getBuildController();

        JTabbedPane tabbedPane = new JTabbedPane();
        ProjectOverviewPanel projectOverviewPanel = new ProjectOverviewPanel(model);
        model.addProjectListener(projectOverviewPanel);
        tabbedPane.addTab("Overview", projectOverviewPanel);

        LogPanel logPanel = new LogPanel();
        buildController.addLogListener(logPanel);
        tabbedPane.addTab("Log", logPanel);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(tabbedPane, BorderLayout.CENTER);
        wrapperPanel.add(new JSeparator(), BorderLayout.SOUTH);
        return wrapperPanel;
    }

    private JPanel createBottomPanel() {
        ITaskController taskController = controller.getTaskController();

        ProgressPanel progressPanel = new ProgressPanel();
        taskController.addActiveTaskListener(progressPanel);

        TaskButton taskButton = new TaskButton(controller.getBuildController());
        taskController.addActiveTaskListener(taskButton);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(true);
        panel.setBackground(Styles.WINDOW_BACKGROUND2);
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
