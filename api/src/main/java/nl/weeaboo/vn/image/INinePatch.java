package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.common.Insets2D;

public interface INinePatch extends Serializable {

    /** Named regions of the 9-patch. */
    public enum AreaId {
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        LEFT,
        CENTER,
        RIGHT,
        BOTTOM_LEFT,
        BOTTOM,
        BOTTOM_RIGHT;

        /**
         * @return {@code true} if this is one of the TOP directions.
         */
        public boolean isTop() {
            return this == TOP_LEFT || this == TOP || this == TOP_RIGHT;
        }

        /**
         * @return {@code true} if this is one of the RIGHT directions.
         */
        public boolean isRight() {
            return this == TOP_RIGHT || this == RIGHT || this == BOTTOM_RIGHT;
        }

        /**
         * @return {@code true} if this is one of the BOTTOM directions.
         */
        public boolean isBottom() {
            return this == BOTTOM_LEFT || this == BOTTOM || this == BOTTOM_RIGHT;
        }

        /**
         * @return {@code true} if this is one of the LEFT directions.
         */
        public boolean isLeft() {
            return this == TOP_LEFT || this == LEFT || this == BOTTOM_LEFT;
        }

    }

    /**
     * @return The intrinsic width for this renderable.
     */
    double getNativeWidth();

    /**
     * @return The intrinsic height for this renderable.
     */
    double getNativeHeight();

    /** Returns the current texture for the requested region. */
    ITexture getTexture(AreaId area);

    /** Sets the texture of the specified region. */
    void setTexture(AreaId area, ITexture texture);

    /** Returns the amount of non-resizable space on the top/right/bottom/left of the 9-patch. */
    Insets2D getInsets();

    /** Sets the amount of non-resizable space on the top/right/bottom/left of the 9-patch. */
    void setInsets(Insets2D i);

    /** Copies all attributes from the other ninepatch to this ninepatch. */
    void set(INinePatch other);
}
