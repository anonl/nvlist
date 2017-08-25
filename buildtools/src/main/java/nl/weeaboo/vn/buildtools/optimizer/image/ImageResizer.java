package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.google.common.primitives.Ints;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionBuilder;

final class ImageResizer implements IImageOperation {

    private final ImageResizerConfig config;

    public ImageResizer(ImageResizerConfig config) {
        this.config = config;
    }

    @Override
    public ImageWithDef optimize(ImageWithDef original) throws IOException {
        ImageDefinition scaledDef = scale(original.getDef());

        Pixmap pixmap = original.getPixmap();
        Pixmap resizedPixmap = PixmapUtil.resizedCopy(pixmap, scaledDef.getSize(), Filter.BiLinear);

        original.dispose();

        return new ImageWithDef(resizedPixmap, scaledDef);
    }

    private ImageDefinition scale(ImageDefinition def) {
        if (!def.getSubRects().isEmpty()) {
            throw new IllegalArgumentException("Resizing images with sub-rects isn't supported yet");
        }

        ImageDefinitionBuilder builder = def.builder();
        builder.setSize(scale(def.getSize()));
        return builder.build();
    }

    private Dim scale(Dim original) {
        double s = config.getScaleFactor();
        return Dim.of(Ints.checkedCast(Math.round(original.w * s)),
                Ints.checkedCast(Math.round(original.h * s)));
    }

}
