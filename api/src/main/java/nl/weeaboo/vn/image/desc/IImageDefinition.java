package nl.weeaboo.vn.image.desc;

import java.util.Collection;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;

public interface IImageDefinition {

    FilePath getFile();
	Dim getSize();

	Collection<? extends IImageSubRect> getSubRects();

	/**
	 * @return The sub-rect with the given id, or {@code null} if no such sub-rect exists.
	 */
	IImageSubRect findSubRect(String id);

	GLScaleFilter getMinifyFilter();
	GLScaleFilter getMagnifyFilter();

	GLTilingMode getTilingModeX();
	GLTilingMode getTilingModeY();

}
