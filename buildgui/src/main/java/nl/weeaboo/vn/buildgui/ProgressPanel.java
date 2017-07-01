package nl.weeaboo.vn.buildgui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import nl.weeaboo.vn.buildgui.task.IActiveTaskListener;
import nl.weeaboo.vn.buildgui.task.IProgressListener;
import nl.weeaboo.vn.buildgui.task.ITask;

/**
 * Panel showing progress for the currently running task.
 */
@SuppressWarnings("serial")
final class ProgressPanel extends JPanel implements IActiveTaskListener {

    private final JProgressBar progressBar;
    private final JLabel messageLabel;

    private final IProgressListener progressListener = new ProgressListener();
    private ITask currentTask;

    public ProgressPanel() {
        progressBar = new JProgressBar();

        messageLabel = new JLabel("");

        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension(100, 40));
        add(progressBar);
        add(messageLabel);
    }

    @Override
    public void onActiveTaskChanged(Optional<ITask> task) {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        if (currentTask != null) {
            currentTask.removeProgressListener(progressListener);
            messageLabel.setText("");
            progressBar.setIndeterminate(false);
        }

        currentTask = task.orElse(null);

        if (currentTask != null) {
            progressBar.setIndeterminate(true);
            messageLabel.setText("");
            currentTask.addProgressListener(progressListener);
        }
    }

    private final class ProgressListener implements IProgressListener {

        @Override
        public void onProgress(String message) {
            SwingUtilities.invokeLater(() -> messageLabel.setText(message));
        }

    }

}
