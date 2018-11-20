package nl.weeaboo.vn.impl.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.filesystem.FilePath;

final class AnalyticsPreloaderStub implements IAnalyticsPreloader {

    private static final long serialVersionUID = 1L;

    private final List<FilePath> preloadImageCalls = new ArrayList<>();

    @Override
    public void preloadImage(FilePath path) {
        preloadImageCalls.add(path);
    }

    public void consumePreloadedImages(String... expected) {
        Assert.assertEquals(Stream.of(expected).map(FilePath::of).collect(Collectors.toList()),
                consumePreloadedImages());
    }

    public List<FilePath> consumePreloadedImages() {
        List<FilePath> result = ImmutableList.copyOf(preloadImageCalls);
        preloadImageCalls.clear();
        return result;
    }
}
