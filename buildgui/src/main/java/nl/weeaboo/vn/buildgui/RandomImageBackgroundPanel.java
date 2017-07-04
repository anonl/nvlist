package nl.weeaboo.vn.buildgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterators;

import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.buildtools.project.ProjectModel;

@SuppressWarnings("serial")
final class RandomImageBackgroundPanel extends ImageBackgroundPanel implements IProjectModelListener {

    private static final Logger LOG = LoggerFactory.getLogger(RandomImageBackgroundPanel.class);

    private @Nullable Timer timer;
    private @Nullable IFileSystem resFileSystem;
    private Iterator<FilePath> imagePathsStream = Collections.emptyIterator();

    public RandomImageBackgroundPanel() {
        SwingHelper.registerVisibilityChangeListener(this, () -> {
            if (isDisplayable()) {
                if (timer == null) {
                    timer = new Timer(5_000, e -> changeImage());
                    timer.start();
                }
            } else {
                if (timer != null) {
                    timer.stop();
                    timer = null;
                }
            }
        });
    }

    @Override
    public void onProjectModelChanged(@Nullable ProjectModel projectModel) {
        if (projectModel == null) {
            resFileSystem = null;
            imagePathsStream = Collections.emptyIterator();
            return;
        }

        resFileSystem = projectModel.getResFileSystem();
        try {
            ArrayList<FilePath> imagePaths = getAvailableImagePaths(resFileSystem);
            Collections.shuffle(imagePaths);
            imagePathsStream = Iterators.cycle(imagePaths);
        } catch (IOException e) {
            LOG.warn("Error while searching for image files", e);
            imagePathsStream = Collections.emptyIterator();
        }
    }

    private void changeImage() {
        if (resFileSystem != null && imagePathsStream.hasNext()) {
            FilePath path = imagePathsStream.next();
            try {
                setImage(SwingImageUtil.readImage(resFileSystem, path.toString()));
                return;
            } catch (IOException e) {
                LOG.warn("Error reading image: {}", path, e);
            }
        }

        // Failure case: no image to show, or an error occurred while trying to show the image
        setImage(null);
    }

    private static ArrayList<FilePath> getAvailableImagePaths(IFileSystem resFileSystem) throws IOException {
        ArrayList<FilePath> result = new ArrayList<>();

        Set<String> supportedExts = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        supportedExts.add("png");
        supportedExts.add("jpg");

        FileCollectOptions collectOpts = FileCollectOptions.files(FilePath.empty());
        collectOpts.setPrefix(FilePath.of("img/"));
        for (FilePath path : resFileSystem.getFiles(collectOpts)) {
            if (path.startsWith(FilePath.of("img/bg")) && supportedExts.contains(path.getExt())) {
                result.add(path);
            }
        }

        return result;
    }

}
