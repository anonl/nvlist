package nl.weeaboo.vn.impl.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;

public final class SizeQualifier implements IResourceQualifier {

    private static final Logger LOG = LoggerFactory.getLogger(SizeQualifier.class);

    private final Dim size;

    public SizeQualifier(Dim size) {
        this.size = Checks.checkNotNull(size);
    }

    static SizeQualifier tryParse(String string) {
        Pattern pattern = Pattern.compile("(\\d+)x(\\d+)");
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            return null;
        }

        try {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));
            return new SizeQualifier(Dim.of(width, height));
        } catch (RuntimeException e) {
            LOG.debug("Found a string that looked like a size qualifier, but wasn't: {}", string);
            return null;
        }
    }

    /**
     * Returns the size (wxh) indicated by this qualifier.
     */
    public Dim getSize() {
        return size;
    }

    @Override
    public String toPathString() {
        return size.w + "x" + size.h;
    }

}
