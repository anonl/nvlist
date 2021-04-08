package nl.weeaboo.vn.desktop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.annotation.CheckForNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;

final class DesktopGraphicsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopGraphicsUtil.class);

    private DesktopGraphicsUtil() {
    }

    static void setWindowIcon(IFileSystem fileSystem) {
        Lwjgl3Window window = getCurrentWindow(Gdx.graphics);

        // Try to load icons in various sizes
        List<Pixmap> pixmaps = Lists.newArrayList();
        try {
            FilePath path = FilePath.of("icon.png");
            try {
                byte[] bytes = FileSystemUtil.readBytes(fileSystem, path);

                LOG.info("Loading icon: {}", path);
                Pixmap fullSize = new Pixmap(bytes, 0, bytes.length);
                /*
                 * Convert to RGBA8888 (libGDX will do this later anyway, doing it now makes resize behavior
                 * more predictable)
                 */
                fullSize = PixmapUtil.convert(fullSize, Format.RGBA8888, true);
                pixmaps.add(fullSize);

                // Derive smaller-sized versions of the icon (if needed)
                Pixmap previousLevel = fullSize;
                while (previousLevel.getWidth() > 16) {
                    Dim targetSize = Dim.of(previousLevel.getWidth() / 2, previousLevel.getHeight() / 2);

                    LOG.debug("Creating resized icon: {}", targetSize);
                    Pixmap pixmap = PixmapUtil.resizedCopy(previousLevel, targetSize, Filter.BiLinear);
                    pixmaps.add(0, pixmap);
                    previousLevel = pixmap;
                }
            } catch (FileNotFoundException fnfe) {
                // File doesn't exist
            } catch (IOException ioe) {
                LOG.warn("Error loading icon: {}", path, ioe);
            }

            window.setIcon(Iterables.toArray(pixmaps, Pixmap.class));
        } finally {
            for (Pixmap pixmap : pixmaps) {
                pixmap.dispose();
            }
        }
    }

    @CheckForNull
    private static Lwjgl3Window getCurrentWindow(Graphics graphics) {
        if (!(graphics instanceof Lwjgl3Graphics)) {
            return null;
        }
        // Oddly, the only public way to get a reference to the main window is through the graphics object...
        return ((Lwjgl3Graphics)Gdx.graphics).getWindow();
    }

    /**
     * @return The new windowed size for the application after applying the safe window size limits.
     */
    public static Dim limitInitialWindowSize(Graphics graphics) {
        if (graphics.isFullscreen()) {
            // If fullscreen, we fill the entire screen already so nothing needs to be done
            return Dim.of(graphics.getBackBufferWidth(), graphics.getBackBufferHeight());
        }

        // Width/height of the window in physical pixels
        int w = graphics.getBackBufferWidth();
        int h = graphics.getBackBufferHeight();

        // Limit window size so it fits inside the current monitor (with a margin for OS bars/decorations)
        DisplayMode displayMode = graphics.getDisplayMode();
        int targetW = Math.min(w, displayMode.width - 100);
        int targetH = Math.min(h, displayMode.height - 150);

        float scale = Math.min(targetW / (float)w, targetH / (float)h);
        targetW = Math.round(w * scale);
        targetH = Math.round(h * scale);
        graphics.setWindowedMode(targetW, targetH);

        return Dim.of(targetW, targetH);
    }

}
