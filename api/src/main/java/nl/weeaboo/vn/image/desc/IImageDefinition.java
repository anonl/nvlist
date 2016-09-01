package nl.weeaboo.vn.image.desc;

import java.util.Collection;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;

public interface IImageDefinition {

    FilePath getFile();
	Dim getSize();

	Collection<? extends IImageSubRect> getSubRects();

	GLScaleFilter getMinifyFilter();
	GLScaleFilter getMagnifyFilter();

	GLTilingMode getTilingModeX();
	GLTilingMode getTilingModeY();

}
