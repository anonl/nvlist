package nl.weeaboo.vn.image.impl.desc;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.FastMath;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.image.desc.GLScaleFilter;
import nl.weeaboo.vn.image.desc.GLTilingMode;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.image.desc.IImageSubRect;

public final class ImageDefinition implements IImageDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(ImageDefinition.class);

    // --- Also update ImageDefinitionJson when changing attributes ---
    private final String filename;
    private final Dim size;
    private final GLScaleFilter minFilter;
    private final GLScaleFilter magFilter;
    private final GLTilingMode wrapX;
    private final GLTilingMode wrapY;
    private final ImmutableList<ImageSubRect> subRects;
    // --- Also update ImageDefinitionJson when changing attributes ---

    public ImageDefinition(String filename, int w, int h) {
        this(filename, Dim.of(w, h),
                GLScaleFilter.DEFAULT, GLScaleFilter.DEFAULT,
                GLTilingMode.DEFAULT, GLTilingMode.DEFAULT,
                ImmutableList.<ImageSubRect>of());
    }
    public ImageDefinition(String filename, Dim size,
            GLScaleFilter minf, GLScaleFilter magf,
            GLTilingMode wrapX, GLTilingMode wrapY,
            Collection<ImageSubRect> subRects) {

        Preconditions.checkArgument(FilePath.of(filename).getName().equals(filename),
                "Filename may not be a path: " + filename);
        this.filename = filename;

        this.size = Checks.checkNotNull(size);
        this.minFilter = Checks.checkNotNull(minf);
        this.magFilter = Checks.checkNotNull(magf);
        this.wrapX = Checks.checkNotNull(wrapX);
        this.wrapY = Checks.checkNotNull(wrapY);

        // Validate extra constraints for tiling textures
        if (wrapX == GLTilingMode.REPEAT || wrapY == GLTilingMode.REPEAT) {
            Checks.checkArgument(FastMath.isPowerOfTwo(size.w, size.h),
                    "Tiling is only supported for textures with power-of-two dimensions");
            Checks.checkArgument(subRects.isEmpty(),
                    "Tiling isn't supported for textures with sub-rects");
        }

        // Validate sub-rects
        Rect bounds = Rect.of(0, 0, size.w, size.h);
        for (ImageSubRect subRect : subRects) {
            Area area = subRect.getArea();
            Checks.checkArgument(bounds.contains(area.x, area.y, area.w, area.h),
                    "Sub-rect " + subRect + " is invalid for image size " + size);
        }
        this.subRects = ImmutableList.copyOf(subRects);
    }

    @Override
    public String toString() {
        return StringUtil.formatRoot("ImageDesc(%s: %dx%d)", filename, size.w, size.h);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public Dim getSize() {
        return size;
    }

    @Override
    public GLScaleFilter getMinifyFilter() {
        return minFilter;
    }

    @Override
    public GLScaleFilter getMagnifyFilter() {
        return magFilter;
    }

    @Override
    public GLTilingMode getTilingModeX() {
        return wrapX;
    }

    @Override
    public GLTilingMode getTilingModeY() {
        return wrapY;
    }

    @Override
    public ImmutableCollection<? extends IImageSubRect> getSubRects() {
        return subRects;
    }

    @Override
    public IImageSubRect findSubRect(String id) {
        for (IImageSubRect subRect : subRects) {
            if (subRect.getId().equals(id)) {
                return subRect;
            }
        }
        LOG.trace("Sub-rect not found: {}#{}", filename, id);
        return null;
    }

}
