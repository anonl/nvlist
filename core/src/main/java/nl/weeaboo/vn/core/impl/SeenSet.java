package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import nl.weeaboo.vn.core.ResourceId;

final class SeenSet implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int EXPECTED_ITEMS = 1000000;
    private static final double FALSE_POSITIVE_PROB = 1e-6;

    private final BloomFilter<ResourceId> seenFiles;

    public SeenSet() {
        seenFiles = BloomFilter.create(ResourceIdFunnel.INSTANCE, EXPECTED_ITEMS, FALSE_POSITIVE_PROB);
    }

    public boolean add(ResourceId resourceId) {
        return seenFiles.put(resourceId);
    }

    public boolean probablyContains(ResourceId resourceId) {
        return seenFiles.mightContain(resourceId);
    }

    // Use an enum to guarantee that the funnel is trivially serializable
    private enum ResourceIdFunnel implements Funnel<ResourceId> {
        INSTANCE;

        @Override
        public void funnel(ResourceId from, PrimitiveSink into) {
            into.putString(from.getCanonicalFilename(), Charsets.UTF_8);
        }
    }
}
