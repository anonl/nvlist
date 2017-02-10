package nl.weeaboo.vn.impl.render.fx;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.image.ITexture;

/**
 * Describes how one or more textures should be composited together into a single image.
 */
public final class ImageCompositeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<TextureEntry> entries = Lists.newArrayList();
    private Dim explicitSize; // Null if no explicit size is set

    public ImageCompositeConfig() {
    }

    /**
     * Adds a texture draw command.
     */
    public void add(TextureEntry entry) {
        entries.add(entry);
    }

    /**
     * Returns a read-only snapshot of the currently buffered entries.
     */
    public ImmutableList<TextureEntry> getEntries() {
        return ImmutableList.copyOf(entries);
    }

    /**
     * @return The size of the result. If no explicit size is set ({@link #setSize(int, int)}), a size is calculated
     *         automatically.
     */
    public Dim getSize() {
        if (explicitSize != null) {
            return explicitSize;
        }
        return getAutoSize();
    }

    private Dim getAutoSize() {
        double w = 0;
        double h = 0;
        for (TextureEntry entry : entries) {
            w = Math.max(w, entry.bounds.x + entry.bounds.w);
            h = Math.max(h, entry.bounds.y + entry.bounds.h);
        }
        return Dim.of((int)Math.ceil(w), (int)Math.ceil(h));
    }

    /**
     * Sets an explicit result size.
     * @see #getSize()
     */
    public void setSize(int w, int h) {
        explicitSize = Dim.of(w, h);
    }

    public static final class TextureEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        private final ITexture texture;

        private Rect2D bounds;
        private BlendMode blendMode = BlendMode.DEFAULT;

        public TextureEntry(ITexture texture) {
            this.texture = texture;
            this.bounds = Rect2D.of(0, 0, texture.getWidth(), texture.getHeight());
        }

        /**
         * @return The texture to render.
         */
        public ITexture getTexture() {
            return texture;
        }

        /**
         * @return The position and size used for rendering.
         */
        public Rect2D getBounds() {
            return bounds;
        }

        /**
         * Sets the top-left x/y position.
         */
        public void setPos(double x, double y) {
            bounds = Rect2D.of(x, y, bounds.w, bounds.h);
        }

        /**
         * @return The blend mode used for rendering.
         */
        public BlendMode getBlendMode() {
            return blendMode;
        }

        /**
         * Sets the blend mode used for rendering.
         */
        public void setBlendMode(BlendMode bm) {
            blendMode = bm;
        }

    }

}
