package nl.weeaboo.vn.buildtools.optimizer;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionBuilder;
import nl.weeaboo.vn.impl.image.desc.ImageSubRect;

public final class ImageWithDefTester {

    private ImageWithDefTester() {
    }

    /**
     * Creates a new {@link ImageWithDef} object from the given pixmap.
     *
     * @param subRects Defined sub-rectangles, will be named {@code "r" + n} in the image definition.
     */
    public static ImageWithDef fromPixmap(Pixmap pixmap, Area... subRects) {
        ImageDefinitionBuilder defBuilder = new ImageDefinitionBuilder("test",
                Dim.of(pixmap.getWidth(), pixmap.getHeight()));
        for (int n = 0; n < subRects.length; n++) {
            defBuilder.addSubRect(new ImageSubRect("r" + n, subRects[n]));
        }
        ImageDefinition def = defBuilder.build();

        return new ImageWithDef(pixmap, def);
    }

}
