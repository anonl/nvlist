package nl.weeaboo.vn.image.desc;

import java.io.Serializable;

import nl.weeaboo.common.Area;

public interface IImageSubRect extends Serializable {

    /** Sub-rect identifier. Unique within the {@link IImageDefinition}. */
    String getId();

    /** Sub-region of the image that this sub-rect represents. */
    Area getArea();

}
