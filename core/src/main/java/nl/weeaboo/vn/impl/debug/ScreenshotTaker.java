package nl.weeaboo.vn.impl.debug;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.graphics.GdxScreenshotUtil;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;

final class ScreenshotTaker {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotTaker.class);

    private @Nullable IScreenshot pendingScreenshot;

    /** Handle input and update internal state. */
    void update(IEnvironment env, INativeInput input) {
        if (pendingScreenshot != null && (pendingScreenshot.isAvailable() || pendingScreenshot.isFailed())) {
            Pixmap pixmap = GdxScreenshotUtil.getPixels(pendingScreenshot);
            if (pixmap != null) {
                writeScreenshot(env.getOutputFileSystem(), pixmap);
            }
            pendingScreenshot.cancel();
            pendingScreenshot = null;
        }

        if (!env.getPref(NovelPrefs.DEBUG)) {
            return; // Debug mode not enabled
        }

        IImageModule imageModule = env.getImageModule();

        // We can't use PRINT_SCREEN, because that key isn't currently supported by libGDX
        if (pendingScreenshot == null && input.consumePress(KeyCode.F12)) {
            pendingScreenshot = imageModule.screenshot();
        }
    }

    private void writeScreenshot(IWritableFileSystem fileSystem, Pixmap pixmap) {
        try {
            byte[] bytes = PixmapUtil.encodePng(pixmap);

            @SuppressWarnings("JdkObsolete") // Use date because Android doesn't have java.time
            String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            FilePath fileName = FilePath.of("screenshot-" + timestamp + ".png");

            FileSystemUtil.writeBytes(fileSystem, fileName, bytes);
            LOG.info("Wrote screenshot: {}", fileName);
        } catch (IOException e) {
            LOG.warn("Unable to write screenshot file", e);
        }
    }

}
