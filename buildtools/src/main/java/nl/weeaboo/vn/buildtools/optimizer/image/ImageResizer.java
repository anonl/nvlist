package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.google.common.primitives.Ints;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Dim;
import nl.weeaboo.common.Rect;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionBuilder;
import nl.weeaboo.vn.impl.image.desc.ImageSubRect;

final class ImageResizer implements IImageOperation {

    private final ImageResizerConfig config;

    public ImageResizer(ImageResizerConfig config) {
        this.config = config;
    }

    @Override
    public ImageWithDef process(ImageWithDef original) throws IOException {
        ImageDefinition originalDef = original.getDef();

        // Scale the image definition
        ImageDefinitionBuilder newDef = originalDef.builder();
        newDef.setSize(scale(originalDef.getSize()));

        // Alter sub-rect definitions to match the scaled dimensions
        newDef.clearSubRects();
        for (ImageSubRect rect : originalDef.getSubRects()) {
            Dim newSize = newDef.getSize();
            Rect bounds = Rect.of(0, 0, newSize.w, newSize.h);
            newDef.addSubRect(scaleSubRect(bounds, rect));
        }

        // Resize the actual pixmap
        Pixmap resizedPixmap = resizedCopy(original.getPixmap());

        return new ImageWithDef(resizedPixmap, newDef.build());
    }

    private ImageSubRect scaleSubRect(Rect outerBounds, ImageSubRect rect) {
        Area originalArea = rect.getArea();

        int scaledX0 = scaleCoord(outerBounds, originalArea.x);
        int scaledY0 = scaleCoord(outerBounds, originalArea.y);
        int scaledX1 = scaleCoord(outerBounds, originalArea.x + originalArea.w);
        int scaledY1 = scaleCoord(outerBounds, originalArea.y + originalArea.h);

        int scaledW = (scaledX0 == scaledX1 ? (originalArea.w < 0 ? -1 : 1) : scaledX1 - scaledX0);
        int scaledH = (scaledY0 == scaledY1 ? (originalArea.h < 0 ? -1 : 1) : scaledY1 - scaledY0);

        Area scaledArea = Area.of(scaledX0, scaledY0, scaledW, scaledH);
        return new ImageSubRect(rect.getId(), scaledArea);
    }

    private Pixmap resizedCopy(Pixmap original) {
        Dim originalSize = Dim.of(original.getWidth(), original.getHeight());
        return PixmapUtil.resizedCopy(original, scale(originalSize), Filter.BiLinear);
    }

    private int scaleCoord(Rect bounds, int original) {
        int scaled = scale(original);
        return Math.max(bounds.x, Math.min(bounds.x + bounds.w, scaled));
    }

    private int scale(int original) {
        return Ints.checkedCast(Math.round(original * config.getScaleFactor()));
    }

    private Dim scale(Dim original) {
        return Dim.of(Math.max(1, scale(original.w)), Math.max(1, scale(original.h)));
    }

}
