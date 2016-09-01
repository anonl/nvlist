package nl.weeaboo.vn.image.desc;

import java.io.Serializable;

import nl.weeaboo.common.Rect;

public interface IImageSubRect extends Serializable {

    String getId();

    Rect getRect();

}
