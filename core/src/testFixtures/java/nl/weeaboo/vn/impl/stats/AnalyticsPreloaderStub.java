package nl.weeaboo.vn.impl.stats;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.MediaType;

final class AnalyticsPreloaderStub implements IAnalyticsPreloader {

    private static final long serialVersionUID = 1L;

    private final ListMultimap<MediaType, FilePath> preloaded = ArrayListMultimap.create();

    @Override
    public void preloadImage(FilePath path) {
        preloaded.put(MediaType.IMAGE, path);
    }

    @Override
    public void preloadSound(FilePath path) {
        preloaded.put(MediaType.SOUND, path);
    }

    public void consumePreloaded(MediaType type, String... expected) {
        Assert.assertEquals(Stream.of(expected).map(FilePath::of).collect(Collectors.toList()),
                consumePreloaded(type));
    }

    public List<FilePath> consumePreloaded(MediaType type) {
        List<FilePath> result = ImmutableList.copyOf(preloaded.get(type));
        preloaded.removeAll(type);
        return result;
    }

}
