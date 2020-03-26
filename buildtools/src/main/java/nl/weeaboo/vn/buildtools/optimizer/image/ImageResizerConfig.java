package nl.weeaboo.vn.buildtools.optimizer.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.buildtools.optimizer.IOptimizerConfig;

/**
 * Configuration related to image resizing.
 */
public final class ImageResizerConfig implements IOptimizerConfig {

    private final Set<Dim> targetResolutions = Sets.newHashSet();

    /**
     * Adds a target resolution for create optimized images for. By default, optimized images are only created
     * for the original resolution. When loading an images, the optimal resolution is automatically chosen
     * based on the resolution of the current window.
     * <p>
     * Note: resolutions larger than the base resolution are ignored, since generating too-large images isn't
     * usually a sensible thing to do.
     */
    public void addTargetResolution(Dim targetResolution) {
        targetResolutions.add(Checks.checkNotNull(targetResolution));
    }

    /**
     * Returns the target resolutions, based on the given original resolution.
     *
     * @see #addTargetResolution(Dim)
     */
    public Collection<Dim> getTargetResolutions(Dim baseResolution) {
        List<Dim> result = new ArrayList<>();

        // Always include the original resolution as well
        result.add(baseResolution);

        // Include all target resolutions smaller than the base
        for (Dim targetResolution : targetResolutions) {
            if (targetResolution.w < baseResolution.w && targetResolution.h < baseResolution.h) {
                result.add(targetResolution);
            }
        }

        return result;
    }

}
