package nl.weeaboo.vn.gdx.graphics;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.base.Stopwatch;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.gdx.HeadlessGdx;

final class PremultiplyBenchmark {

    private static final Logger LOG = LoggerFactory.getLogger(PremultiplyBenchmark.class);

    private Pixmap pixmap;

    private PremultiplyBenchmark() {
        HeadlessGdx.init();

        pixmap = new Pixmap(Gdx.files.classpath("img/test.png"));
        pixmap = PixmapUtil.convert(pixmap, Format.RGBA8888, true);
        // Resize to a large standard resolution
        pixmap = PixmapUtil.resizedCopy(pixmap, Dim.of(1024, 1024), Filter.BiLinear);
    }

    public void dispose() {
        pixmap.dispose();
    }

    /** Premultiply alpha. */
    private void run() {
        PremultUtil.premultiplyAlpha(pixmap);
    }

    public static void main(String[] args) {
        Stopwatch total = Stopwatch.createUnstarted();

        int warmup = 10;
        int numRuns = 50;
        for (int run = 1; run <= numRuns; run++) {
            PremultiplyBenchmark benchmark = new PremultiplyBenchmark();
            try {
                if (run > warmup) {
                    total.start();
                }

                Stopwatch stopwatch = Stopwatch.createStarted();
                benchmark.run();
                LOG.info("Run {}/{} took {}", run, numRuns, stopwatch);

                if (total.isRunning()) {
                    total.stop();
                }
            } finally {
                benchmark.dispose();
            }
        }

        LOG.info("Total took {} ms per run",
                total.elapsed(TimeUnit.MILLISECONDS) / (double)(numRuns - warmup));
    }

}
