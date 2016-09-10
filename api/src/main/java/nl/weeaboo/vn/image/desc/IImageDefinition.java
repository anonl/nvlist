package nl.weeaboo.vn.image.desc;

import java.util.Collection;

import nl.weeaboo.common.Dim;

public interface IImageDefinition {

    String getFilename();
	Dim getSize();

	Collection<? extends IImageSubRect> getSubRects();

	/** @return The sub-rect with the given ID, or {@code null} if not found. */
	IImageSubRect findSubRect(String id);

	GLScaleFilter getMinifyFilter();
	GLScaleFilter getMagnifyFilter();

	GLTilingMode getTilingModeX();
	GLTilingMode getTilingModeY();

}
