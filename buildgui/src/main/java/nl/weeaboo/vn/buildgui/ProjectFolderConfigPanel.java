package nl.weeaboo.vn.buildgui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

@SuppressWarnings("serial")
final class ProjectFolderConfigPanel extends JPanel implements IProjectModelListener {

    private final IBuildGuiController guiController;

    private final JButton createProjectButton;
    private final JButton openProjectButton;
    private final JLabel projectFolderLabel;

    public ProjectFolderConfigPanel(IBuildGuiController guiController) {
        this.guiController = Objects.requireNonNull(guiController);

        createProjectButton = new JButton("Create new project");
        createProjectButton.addActionListener(e -> doCreateProject());

        openProjectButton = new JButton("Open existing project");
        openProjectButton.addActionListener(e -> doOpenProject());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder());
        buttonPanel.add(createProjectButton);
        buttonPanel.add(openProjectButton);

        projectFolderLabel = new JLabel();
        projectFolderLabel.setMaximumSize(new Dimension(999, 999));

        setBackground(Styles.HEADER_BACKGROUND2);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
        add(buttonPanel);
        add(Box.createVerticalStrut(5));
        projectFolderLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(projectFolderLabel);

        refresh();
    }

    private void refresh() {
        projectFolderLabel.setText(" ");

        guiController.getModel().getProject().ifPresent(project -> {
            File projectFolder = project.getFolderConfig().getProjectFolder();
            projectFolderLabel.setText("Project folder: "
                    + ProjectFolderConfig.toCanonicalPath(projectFolder));
        });
    }

    private void doCreateProject() {
        if (doOpenProject()) {
            IBuildController buildController = guiController.getBuildController();
            buildController.startInitProjectTask();
        }
    }

    private boolean doOpenProject() {
        BuildGuiModel model = guiController.getModel();

        ProjectFolderConfig folderConfig = model.getProject()
                .map(NvlistProjectConnection::getFolderConfig)
                .orElseGet(ProjectFolderConfig::new);

        Optional<File> newProjectFolder = selectFolder(folderConfig.getProjectFolder());
        if (!newProjectFolder.isPresent()) {
            return false; // Folder selection was cancelled
        }

        model.setProjectFolders(folderConfig.withProjectFolder(newProjectFolder.get()));
        return true;
    }

    private Optional<File> selectFolder(File initialSelection) {
        JFileChooser fileChooser = new JFileChooser(initialSelection);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select folder");
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return Optional.empty();
        }

        return Optional.ofNullable(fileChooser.getSelectedFile());
    }

    @Override
    public void onProjectChanged(NvlistProjectConnection projectModel) {
        refresh();
    }

}
