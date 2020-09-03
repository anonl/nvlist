package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

/**
 * Provides easy access to the built-in assets.
 */
public final class BuiltinAssets {

    private static final Logger LOG = LoggerFactory.getLogger(BuiltinAssets.class);
    private static final BuiltinAssets INSTANCE = new BuiltinAssets();

    private final ImmutableSet<String> assets;

    private BuiltinAssets() {
        Collection<String> relPaths = ImmutableSet.of();
        try {
            String assetsList = Resources.toString(getClass().getResource("/builtin/assets.list"),
                    StandardCharsets.UTF_8);
            relPaths = Splitter.on('\n').trimResults().omitEmptyStrings().splitToList(assetsList);
        } catch (IOException ioe) {
            LOG.warn("Unable to parse builtin assets list", ioe);
        }
        assets = ImmutableSet.copyOf(relPaths);
    }

    public static Collection<String> getScripts() {
        return Collections2.filter(INSTANCE.assets, relPath -> relPath.startsWith("script/"));
    }

    public static String readString(String relPath) throws IOException {
        return Resources.toString(BuiltinAssets.class.getResource("/builtin/" + relPath), StandardCharsets.UTF_8);
    }

}
