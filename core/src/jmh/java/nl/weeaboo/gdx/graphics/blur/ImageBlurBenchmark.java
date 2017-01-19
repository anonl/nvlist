package nl.weeaboo.gdx.graphics.blur;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.common.Dim;
import nl.weeaboo.gdx.HeadlessGdx;
import nl.weeaboo.gdx.graphics.PixmapUtil;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ImageBlurBenchmark {

    @State(Scope.Thread)
    public static class Input {

        public Pixmap pixmap;

        @Setup(Level.Trial)
        public void init() {
            HeadlessGdx.init();

            pixmap = new Pixmap(Gdx.files.classpath("img/test.png"));
            pixmap = PixmapUtil.convert(pixmap, Format.RGBA8888, true);
            // Resize to a large standard resolution
            pixmap = PixmapUtil.resizedCopy(pixmap, Dim.of(1920, 1080), Filter.BiLinear);
        }

        @TearDown(Level.Trial)
        public void dispose() {
            pixmap.dispose();
        }

    }

    @Benchmark
    public Pixmap blur7(Input input) {
        return doBlur(input.pixmap, 7);
    }

    @Benchmark
    public Pixmap blur15(Input input) {
        return doBlur(input.pixmap, 15);
    }

    @Benchmark
    public Pixmap blur32(Input input) {
        return doBlur(input.pixmap, 32);
    }

    private Pixmap doBlur(Pixmap original, int radius) {
        int scale = 2;
        Dim scaledSize = Dim.of(original.getWidth() / scale, original.getHeight() / scale);

        Pixmap blurred = PixmapUtil.resizedCopy(original, scaledSize, Filter.NearestNeighbour);

        ImageBlur imageBlur = new ImageBlur();
        imageBlur.setRadius(radius / scale);
        imageBlur.applyBlur(blurred);

        blurred.dispose();
        return blurred;
    }

}
