package nl.weeaboo.gdx.graphics.blur;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.google.common.base.Stopwatch;

import nl.weeaboo.common.Dim;
import nl.weeaboo.gdx.HeadlessGdx;
import nl.weeaboo.gdx.graphics.PixmapUtil;

public class ImageBlurBenchmark {

    public static void main(String[] args) throws IOException {
        HeadlessGdx.init();

        Pixmap original = new Pixmap(Gdx.files.classpath("img/test.png"));
        original = PixmapUtil.convert(original, Format.RGBA8888, true);
        // Resize to a large standard resolution
        original = PixmapUtil.resizedCopy(original, Dim.of(1920, 1080), Filter.BiLinear);

        ImageBlur imageBlur = new ImageBlur();
        Pixmap blurred = PixmapUtil.copy(original);
        for (int radius : new int[] {7, 15, 32}) {
            int repetitions = 500;

            Stopwatch kernelTime = Stopwatch.createUnstarted();
            Stopwatch prepareTime = Stopwatch.createUnstarted();

            for (int n = 0; n < repetitions; n++) {
                // Reset image back to original
                /*
                PixmapUtil.copySubRect(original, Rect.of(0, 0, original.getWidth(), original.getHeight()),
                        blurred, Rect.of(0, 0, original.getWidth(), original.getHeight()),
                        Filter.NearestNeighbour);
                */

                prepareTime.start();
                int scale = 2;
                Dim scaledSize = Dim.of(original.getWidth() / scale, original.getHeight() / scale);

                blurred.dispose();
                blurred = PixmapUtil.resizedCopy(original, scaledSize, Filter.NearestNeighbour);

                imageBlur.setRadius(radius / scale);
                prepareTime.stop();

                kernelTime.start();
                imageBlur.applyBlur(blurred);
                kernelTime.stop();
            }

            double prepareMs = prepareTime.elapsed(TimeUnit.MILLISECONDS);
            double kernelMs = kernelTime.elapsed(TimeUnit.MILLISECONDS);
            double totalMs = prepareMs + kernelMs;
            System.out.printf("Radius %d: %.0f+%.0f=%.0fms (%.1f+%.1f=%.1fms per run)\n",
                    radius, prepareMs, kernelMs, totalMs,
                    prepareMs / repetitions, kernelMs / repetitions, totalMs / repetitions);

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
