package nl.weeaboo.vn.buildgui;

import java.awt.Dimension;
import java.awt.FlowLayout;
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

import nl.weeaboo.vn.buildgui.task.IActiveTaskListener;
import nl.weeaboo.vn.buildtools.task.ITask;

@SuppressWarnings("serial")
final class TaskButton extends JPanel implements IActiveTaskListener {

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
        buildController.startRunTask();
    }

    private void handleOtherTaskButtonPress() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem item = new JMenuItem("Create release");
        item.addActionListener(e -> buildController.startAssembleDistTask());
        menu.add(item);

        menu.show(otherTaskButton, 0, 0);
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
