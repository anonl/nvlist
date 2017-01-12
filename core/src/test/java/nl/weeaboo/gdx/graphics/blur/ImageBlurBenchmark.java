package nl.weeaboo.gdx.graphics.blur;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.google.common.base.Stopwatch;

import nl.weeaboo.common.Rect;
import nl.weeaboo.gdx.HeadlessGdx;
import nl.weeaboo.gdx.graphics.PixmapUtil;

public class ImageBlurBenchmark {

    public static void main(String[] args) throws IOException {
        HeadlessGdx.init();

        Pixmap original = new Pixmap(Gdx.files.classpath("img/a.png"));
        original = PixmapUtil.convert(original, Format.RGBA8888, true);

        Pixmap blurred = PixmapUtil.copy(original);
        for (int radius : new int[] {7, 8, 15, 32}) {
            int repetitions = 100;
            Stopwatch kernelTime = Stopwatch.createStarted();
            for (int n = 0; n < repetitions; n++) {
                kernelTime.stop();

                // Reset image back to original
                PixmapUtil.copySubRect(original, Rect.of(0, 0, original.getWidth(), original.getHeight()),
                        blurred, Rect.of(0, 0, original.getWidth(), original.getHeight()));

                kernelTime.start();
                ImageBlur.blur(blurred, radius);
            }
            System.out.printf("Radius %d: %s (%dms per run)\n", radius, kernelTime,
                    kernelTime.elapsed(TimeUnit.MILLISECONDS) / repetitions);

            PixmapIO.PNG encoder = new PixmapIO.PNG();
            encoder.setFlipY(false);
            try (OutputStream out = new FileOutputStream(new File("blur" + radius + ".png"))) {
                encoder.write(out, blurred);
            }
        }
        blurred.dispose();
        original.dispose();
    }

}
