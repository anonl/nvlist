package nl.weeaboo.vn.image.desc;

import java.io.Serializable;

import nl.weeaboo.common.Area;

public interface IImageSubRect extends Serializable {

    String getId();

    Area getArea();

}
