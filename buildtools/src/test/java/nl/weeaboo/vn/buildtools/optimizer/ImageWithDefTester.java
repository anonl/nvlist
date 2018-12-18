package nl.weeaboo.vn.buildtools.optimizer;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

public final class ImageWithDefTester {

    private ImageWithDefTester() {
    }

    /** Creates a new {@link ImageWithDef} object from the given pixmap. */
    public static ImageWithDef fromPixmap(Pixmap pixmap) {
        ImageDefinition def = new ImageDefinition("test", Dim.of(pixmap.getWidth(), pixmap.getHeight()));
        return new ImageWithDef(pixmap, def);
    }

}
