package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.buildgui.task.IActiveTaskListener;
import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.buildtools.task.ITask;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.NovelPrefs;

@SuppressWarnings("serial")
final class ProjectOverviewPanel extends JPanel implements IProjectModelListener, IActiveTaskListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectOverviewPanel.class);

    private final BuildGuiModel model;
    private final TopPanel topPanel;
    private final SlideShow slideShow;

    public ProjectOverviewPanel(BuildGuiModel model) {
        this.model = Objects.requireNonNull(model);

        topPanel = new TopPanel();
        slideShow = new SlideShow();

        SwingTimer.startAnimation(this, 5_000, () -> {
            slideShow.loadRandomImage();
            repaint();
        });

        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);

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
        NvlistProjectConnection maybeProject = model.getProject().orElse(null);
        topPanel.onProjectModelChanged(maybeProject);
        slideShow.onProjectModelChanged(maybeProject);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        slideShow.paint(g, getWidth(), getHeight());
    }

    private static final class TopPanel extends JPanel {

        private final JLabel iconLabel;
        private final JLabel titleLabel;
        private final JButton browseProjectButton;
        private final JButton browseBuildToolsButton;

        private final BufferedImage missingIcon;

        private @Nullable ProjectFolderConfig folderConfig;

        public TopPanel() {
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
            buttonPanel.setOpaque(false);
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.add(browseProjectButton);
            buttonPanel.add(browseBuildToolsButton);
            buttonPanel.add(Box.createGlue());

            setOpaque(false);
            setBackground(Styles.GLASS_BACKGROUND);

            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setLayout(new BorderLayout(10, 10));
            add(iconLabel, BorderLayout.WEST);
            add(titleLabel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.EAST);

            resetState();
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
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

    private static final class SlideShow {

        private @Nullable IFileSystem fileSystem;
        private ImmutableList<FilePath> imagePaths;
        private @Nullable BufferedImage currentImage;

        public SlideShow() {
            resetState();
        }

        private void resetState() {
            fileSystem = null;
            imagePaths = ImmutableList.of();
            currentImage = null;
        }

        private void onProjectModelChanged(@Nullable NvlistProjectConnection projectModel) {
            resetState();

            if (projectModel != null) {
                fileSystem = projectModel.getResFileSystem();

                try {
                    FileCollectOptions imageFilter = FileCollectOptions.files(
                        MediaType.IMAGE.getSubFolder().resolve("bg"));
                    imagePaths = ImmutableList.copyOf(Iterables.filter(fileSystem.getFiles(imageFilter),
                        path -> "jpg".equalsIgnoreCase(path.getExt())));

                    loadRandomImage();
                } catch (IOException e) {
                    LOG.warn("Unable to list image files in project", e);
                }
            }
        }

        void paint(Graphics g, int w, int h) {
            if (currentImage != null) {
                double scale = Math.max(w / (double)currentImage.getWidth(), h / (double)currentImage.getHeight());
                int iw = (int)(scale * currentImage.getWidth());
                int ih = (int)(scale * currentImage.getHeight());

                g.drawImage(currentImage, (w - iw) / 2, (h - ih) / 2, iw, ih, null);
            }
        }

        private void loadRandomImage() {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (imagePaths.size() > 0) {
                FilePath imagePath = imagePaths.get(random.nextInt(imagePaths.size()));
                try {
                    BufferedImage image = SwingImageUtil.readImage(fileSystem, imagePath);
                    image = SwingImageUtil.scaledImage(image, 480, 270);
                    currentImage = image;
                } catch (IOException ioe) {
                    LOG.warn("Error loading image: {}", imagePath, ioe);
                }
            }
        }
    }

}
