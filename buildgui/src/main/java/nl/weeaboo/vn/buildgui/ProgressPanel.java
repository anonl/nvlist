package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import nl.weeaboo.vn.buildgui.task.IActiveTaskListener;
import nl.weeaboo.vn.buildtools.task.IProgressListener;
import nl.weeaboo.vn.buildtools.task.ITask;
import nl.weeaboo.vn.buildtools.task.TaskResultType;

/**
 * Panel showing progress for the currently running task.
 */
@SuppressWarnings("serial")
final class ProgressPanel extends JPanel implements IActiveTaskListener {

    private final ImageIcon progressIcon;
    private final ImageIcon failedIcon;

    private final JLabel progressIndicator;
    private final JLabel messageLabel;

    private final IProgressListener progressListener = new ProgressListener();
    private ITask currentTask;

    public ProgressPanel() {
        progressIcon = new ImageIcon(getClass().getResource("task-progress.gif"));
        failedIcon = new ImageIcon(getClass().getResource("task-failed.gif"));

        progressIndicator = new JLabel();
        progressIndicator.setPreferredSize(new Dimension(progressIcon.getIconWidth(),
                progressIcon.getIconHeight()));

        messageLabel = new JLabel("");

        setOpaque(false);
        setLayout(new BorderLayout(10, 10));
        add(progressIndicator, BorderLayout.WEST);
        add(messageLabel, BorderLayout.CENTER);
    }

    @Override
    public void onActiveTaskChanged(Optional<ITask> task) {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        if (currentTask != null) {
            currentTask.removeProgressListener(progressListener);
        }

        currentTask = task.orElse(null);

        if (currentTask != null) {
            progressIndicator.setIcon(progressIcon);
            messageLabel.setText("");
            currentTask.addProgressListener(progressListener);
        }
    }

    private final class ProgressListener implements IProgressListener {

        @Override
        public void onProgress(String message) {
            SwingUtilities.invokeLater(() -> {
                messageLabel.setText(message);
            });
        }

        @Override
        public void onFinished(TaskResultType resultType, String message) {
            SwingUtilities.invokeLater(() -> {
                if (resultType == TaskResultType.FAILED) {
                    progressIndicator.setIcon(failedIcon);
                    messageLabel.setText(message);
                } else {
                    progressIndicator.setIcon(null);
                    messageLabel.setText("");
                }
            });
        }

    }

}
