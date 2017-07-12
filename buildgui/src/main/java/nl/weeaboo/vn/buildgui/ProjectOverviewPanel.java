package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.buildgui.task.IActiveTaskListener;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.buildtools.task.ITask;
import nl.weeaboo.vn.core.NovelPrefs;

@SuppressWarnings("serial")
final class ProjectOverviewPanel extends JPanel implements IProjectModelListener, IActiveTaskListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectOverviewPanel.class);

    private final BuildGuiModel model;
    private final HeaderPanel headerPanel;

    public ProjectOverviewPanel(BuildGuiModel model) {
        this.model = Objects.requireNonNull(model);

        headerPanel = new HeaderPanel();

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);

        refresh();
    }

    @Override
    public void onProjectChanged(NvlistProjectConnection projectModel) {
        refresh();
    }

    @Override
    public void onActiveTaskChanged(Optional<ITask> currentTask) {
        refresh();
    }

    private void refresh() {
        headerPanel.onProjectModelChanged(model.getProject().orElse(null));
    }

    private static final class HeaderPanel extends JPanel {

        private final JLabel iconLabel;
        private final JLabel titleLabel;
        private final JButton browseProjectButton;
        private final JButton browseBuildToolsButton;

        private final BufferedImage missingIcon;

        private @Nullable ProjectFolderConfig folderConfig;

        public HeaderPanel() {
            BufferedImage missingIcon;
            try {
                missingIcon = ImageIO.read(getClass().getResource("missing-icon.png"));
            } catch (IOException ioe) {
                missingIcon = SwingHelper.newBufferedImage(1, 1);
            }
            this.missingIcon = missingIcon;

            iconLabel = new JLabel();

            titleLabel = new JLabel();
            titleLabel.setVerticalAlignment(JLabel.TOP);
            Styles.applyTitleFont(titleLabel);

            browseProjectButton = new JButton("Open project folder");
            browseProjectButton.setMaximumSize(new Dimension(999, 999));
            browseProjectButton.addActionListener(e -> doBrowse(ProjectFolderConfig::getProjectFolder));

            browseBuildToolsButton = new JButton("Open engine folder");
            browseBuildToolsButton.setMaximumSize(new Dimension(999, 999));
            browseBuildToolsButton.addActionListener(e -> doBrowse(ProjectFolderConfig::getBuildToolsFolder));

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.add(browseProjectButton);
            buttonPanel.add(browseBuildToolsButton);
            buttonPanel.add(Box.createGlue());

            setLayout(new BorderLayout(10, 10));
            add(iconLabel, BorderLayout.WEST);
            add(titleLabel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.EAST);

            resetState();
        }

        private void resetState() {
            folderConfig = null;

            iconLabel.setIcon(null);
            titleLabel.setText("");
            browseProjectButton.setVisible(false);
            browseBuildToolsButton.setVisible(false);
        }

        private void onProjectModelChanged(@Nullable NvlistProjectConnection projectModel) {
            resetState();

            if (projectModel != null) {
                folderConfig = projectModel.getFolderConfig();

                IFileSystem fileSystem = projectModel.getResFileSystem();
                BufferedImage icon;
                try {
                    icon = SwingImageUtil.readImage(fileSystem, "icon.png");
                } catch (IOException ioe) {
                    LOG.warn("Project icon not found: {}", ioe.toString());
                    icon = missingIcon;
                }
                icon = SwingImageUtil.scaledImage(icon, 64, 64);
                iconLabel.setIcon(new ImageIcon(icon));

                titleLabel.setText(projectModel.getPref(NovelPrefs.TITLE));

                browseProjectButton.setVisible(true);
                browseBuildToolsButton.setVisible(true);
            }
        }

        private void doBrowse(Function<ProjectFolderConfig, File> folderGetter) {
            if (folderConfig != null && Desktop.isDesktopSupported()) {
                File folder = folderGetter.apply(folderConfig);
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(folder.getCanonicalFile().toURI());
                } catch (IOException e) {
                    LOG.warn("Error opening browser for: {}", folder, e);
                }
            }
        }

    }

}
