package nl.weeaboo.vn.impl.image.desc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.image.desc.GLScaleFilter;
import nl.weeaboo.vn.image.desc.GLTilingMode;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.image.desc.IImageSubRect;

public final class ImageDefinitionBuilder implements IImageDefinition {

    private String filename;
    private Dim size;

    private GLScaleFilter minFilter = GLScaleFilter.DEFAULT;
    private GLScaleFilter magFilter = GLScaleFilter.DEFAULT;
    private GLTilingMode tileX = GLTilingMode.DEFAULT;
    private GLTilingMode tileY = GLTilingMode.DEFAULT;

    private final List<ImageSubRect> subRects = Lists.newArrayList();

    public ImageDefinitionBuilder(String filename, Dim size) {
        this.filename = Checks.checkNotNull(filename);
        this.size = Checks.checkNotNull(size);
    }

    public ImageDefinitionBuilder(ImageDefinition original) {
        this(original.getFilename(), original.getSize());

        minFilter = original.getMinifyFilter();
        magFilter = original.getMagnifyFilter();
        tileX = original.getTilingModeX();
        tileY = original.getTilingModeY();

        subRects.addAll(original.getSubRects());
    }

    /** Creates a new immutable {@link ImageDefinition} instance. */
    public ImageDefinition build() {
        return new ImageDefinition(this);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * @see #getFilename()
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public Dim getSize() {
        return size;
    }

    /**
     * @see #getSize()
     */
    public void setSize(Dim size) {
        this.size = size;
    }

    @Override
    public GLScaleFilter getMinifyFilter() {
        return minFilter;
    }

    /**
     * @see #getMinifyFilter()
     */
    public void setMinifyFilter(GLScaleFilter minFilter) {
        this.minFilter = minFilter;
    }

    @Override
    public GLScaleFilter getMagnifyFilter() {
        return magFilter;
    }

    /**
     * @see #getMagnifyFilter()
     */
    public void setMagnifyFilter(GLScaleFilter magFilter) {
        this.magFilter = magFilter;
    }

    @Override
    public GLTilingMode getTilingModeX() {
        return tileX;
    }

    /**
     * @see #getTilingModeX()
     */
    public void setTilingModeX(GLTilingMode tileX) {
        this.tileX = tileX;
    }

    @Override
    public GLTilingMode getTilingModeY() {
        return tileY;
    }

    /**
     * @see #getTilingModeY()
     */
    public void setTilingModeY(GLTilingMode tileY) {
        this.tileY = tileY;
    }

    @Override
    public Collection<ImageSubRect> getSubRects() {
        return Collections.unmodifiableList(subRects);
    }

    /** Removes all sub-rects. */
    public void clearSubRects() {
        subRects.clear();
    }

    /** Adds an additional sub-rect. */
    public void addSubRect(ImageSubRect subRect) {
        subRects.add(subRect);
    }

    /** Overwrites the available sub-rects. */
    public void setSubRects(Iterable<ImageSubRect> newRects) {
        subRects.clear();
        Iterables.addAll(subRects, newRects);
    }

    @Override
    public @Nullable IImageSubRect findSubRect(String id) {
        return ImageDefinition.findSubRect(subRects, id);
    }

}
