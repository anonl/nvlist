package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JPanel;

import nl.weeaboo.vn.buildgui.task.IActiveTaskListener;
import nl.weeaboo.vn.buildgui.task.ITask;

@SuppressWarnings("serial")
final class TaskButton extends JPanel implements IActiveTaskListener {

    private final IBuildController buildController;
    private final JButton runCancelButton;

    private @Nullable ITask activeTask;

    public TaskButton(IBuildController buildController) {
        this.buildController = Objects.requireNonNull(buildController);

        runCancelButton = new JButton();
        runCancelButton.setFont(runCancelButton.getFont().deriveFont(Font.BOLD, 18f));
        runCancelButton.setPreferredSize(new Dimension(150, 50));
        runCancelButton.addActionListener(e -> handleRunCancelButtonPress());

        // TODO: Add button(s) to run other Gradle tasks
        setLayout(new BorderLayout());
        add(runCancelButton, BorderLayout.CENTER);

        updateState();
    }

    private void handleRunCancelButtonPress() {
        if (activeTask != null) {
            activeTask.cancel();
        } else {
            buildController.startRunTask();
        }
    }

    @Override
    public void onActiveTaskChanged(Optional<ITask> currentTask) {
        activeTask = currentTask.orElse(null);

        updateState();
    }

    private void updateState() {
        if (activeTask != null) {
            runCancelButton.setText("Cancel");
        } else {
            runCancelButton.setText("Run Game");
        }
    }

}
