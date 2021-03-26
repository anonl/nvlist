package nl.weeaboo.vn.buildgui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.buildgui.task.IActiveTaskListener;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.buildtools.task.IProgressListener;
import nl.weeaboo.vn.buildtools.task.ITask;
import nl.weeaboo.vn.buildtools.task.TaskResultType;

@SuppressWarnings("serial")
final class TaskButton extends JPanel implements IActiveTaskListener {

    private static final Logger LOG = LoggerFactory.getLogger(TaskButton.class);

    private final IBuildController buildController;
    private final JButton cancelButton;
    private final JButton runButton;
    private final JButton otherTaskButton;

    private @Nullable ITask activeTask;

    public TaskButton(IBuildController buildController) {
        this.buildController = Objects.requireNonNull(buildController);

        cancelButton = createLargeButton("Cancel");
        cancelButton.addActionListener(e -> doCancel());

        runButton = createLargeButton("Run Game");
        runButton.addActionListener(e -> doRunGame());

        otherTaskButton = new JButton();
        otherTaskButton.setIcon(new ImageIcon(getClass().getResource("more-options.png")));
        otherTaskButton.setPreferredSize(new Dimension(35, 35));
        otherTaskButton.addActionListener(e -> handleOtherTaskButtonPress());

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        add(cancelButton);
        add(Box.createHorizontalStrut(5));
        add(runButton);
        add(otherTaskButton);

        refresh();
    }

    private static JButton createLargeButton(String label) {
        JButton button = new JButton(label);
        Styles.applyLargerFont(button);
        button.setPreferredSize(new Dimension(125, 35));
        return button;
    }

    private void doCancel() {
        if (activeTask != null) {
            activeTask.cancel();
        }
    }

    private void doRunGame() {
        buildController.startRun();
    }

    private void handleOtherTaskButtonPress() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem assembleDistitem = new JMenuItem("Create release");
        assembleDistitem.addActionListener(e -> createRelease());
        menu.add(assembleDistitem);

        JMenuItem optimizeResourcesItem = new JMenuItem("Optimize resources");
        optimizeResourcesItem.addActionListener(e -> buildController.startResourceOptimizer());
        menu.add(optimizeResourcesItem);

        JMenuItem checkForUpdatesItem = new JMenuItem("Check for updates");
        checkForUpdatesItem.addActionListener(e -> buildController.startCheckForUpdates());
        menu.add(checkForUpdatesItem);

        menu.show(otherTaskButton, 0, 0);
    }

    private void createRelease() {
        ITask task = buildController.startCreateRelease();
        task.addProgressListener(new IProgressListener() {
            @Override
            public void onFinished(TaskResultType resultType, String message) {
                if (resultType != TaskResultType.SUCCESS) {
                    return;
                }

                ProjectFolderConfig folderConfig = buildController.getFolderConfig();
                Path releaseFolder = folderConfig.getBuildOutFolder().resolve("desktop/release");
                if (!Files.isDirectory(releaseFolder)) {
                    return;
                }

                // Show releases folder
                try {
                    Desktop.getDesktop().open(releaseFolder.toFile());
                } catch (IOException e) {
                    LOG.warn("Unable to show build folder", e);
                }
            }
        });
    }

    @Override
    public void onActiveTaskChanged(Optional<ITask> currentTask) {
        activeTask = currentTask.orElse(null);

        refresh();
    }

    private void refresh() {
        final boolean running = activeTask != null;

        cancelButton.setVisible(running);
        runButton.setEnabled(!running);
        otherTaskButton.setEnabled(!running);
    }

}
