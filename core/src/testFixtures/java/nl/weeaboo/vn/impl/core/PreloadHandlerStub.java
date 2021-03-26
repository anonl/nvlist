package nl.weeaboo.vn.impl.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.vn.core.ResourceId;

final class PreloadHandlerStub implements IPreloadHandler {

    private static final long serialVersionUID = 1L;

    private final List<ResourceId> preloaded = new ArrayList<>();

    @Override
    public void preloadNormalized(ResourceId resourceId) {
        preloaded.add(resourceId);
    }

    void consumePreloaded(ResourceId... expected) {
        List<ResourceId> snapshot = ImmutableList.copyOf(preloaded);
        preloaded.clear();

        Assert.assertEquals(Arrays.asList(expected), snapshot);
    }
}
