package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import nl.weeaboo.vn.buildgui.task.IActiveTaskListener;
import nl.weeaboo.vn.buildtools.task.ITask;

@SuppressWarnings("serial")
final class TaskButton extends JPanel implements IActiveTaskListener {

    private final IBuildController buildController;
    private final JButton runCancelButton;
    private final JButton otherTaskButton;

    private @Nullable ITask activeTask;

    public TaskButton(IBuildController buildController) {
        this.buildController = Objects.requireNonNull(buildController);

        runCancelButton = new JButton();
        Styles.applyLargerFont(runCancelButton);
        runCancelButton.setPreferredSize(new Dimension(125, 35));
        runCancelButton.addActionListener(e -> handleRunCancelButtonPress());

        otherTaskButton = new JButton();
        otherTaskButton.setIcon(new ImageIcon(getClass().getResource("more-options.png")));
        otherTaskButton.setPreferredSize(new Dimension(35, 35));
        otherTaskButton.addActionListener(e -> handleOtherTaskButtonPress());

        setLayout(new BorderLayout());
        add(runCancelButton, BorderLayout.CENTER);
        add(otherTaskButton, BorderLayout.EAST);

        updateState();
    }

    private void handleRunCancelButtonPress() {
        if (activeTask != null) {
            activeTask.cancel();
        } else {
            buildController.startRunTask();
        }
    }

    private void handleOtherTaskButtonPress() {
        JPopupMenu menu = new JPopupMenu("test");

        JMenuItem item = new JMenuItem("Create release");
        item.addActionListener(e -> buildController.startAssembleDistTask());
        menu.add(item);

        menu.show(otherTaskButton, 0, 0);
    }

    @Override
    public void onActiveTaskChanged(Optional<ITask> currentTask) {
        activeTask = currentTask.orElse(null);

        updateState();
    }

    private void updateState() {
        final boolean running = activeTask != null;

        if (running) {
            runCancelButton.setText("Cancel");
        } else {
            runCancelButton.setText("Run Game");
        }
        otherTaskButton.setEnabled(!running);
    }

}
