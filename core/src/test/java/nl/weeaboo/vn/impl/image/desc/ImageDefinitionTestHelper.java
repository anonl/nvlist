package nl.weeaboo.vn.impl.image.desc;

import org.junit.Assert;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.image.desc.IImageSubRect;

final class ImageDefinitionTestHelper {

    public ImageDefinition findDefById(Iterable<ImageDefinition> available, String filename) {
        for (ImageDefinition def : available) {
            if (def.getFilename().equals(filename)) {
                return def;
            }
        }
        throw new AssertionError("Not found: " + filename);
    }

    public void assertEquals(ImageDefinition expected, ImageDefinition actual) {
        Assert.assertEquals(expected.getFilename(), actual.getFilename());
        Assert.assertEquals(expected.getSize(), actual.getSize());
        Assert.assertEquals(expected.getMinifyFilter(), actual.getMinifyFilter());
        Assert.assertEquals(expected.getMagnifyFilter(), actual.getMagnifyFilter());
        Assert.assertEquals(expected.getTilingModeX(), actual.getTilingModeX());
        Assert.assertEquals(expected.getTilingModeY(), actual.getTilingModeY());

        for (IImageSubRect subRect : expected.getSubRects()) {
            assertEquals(subRect, actual.findSubRect(subRect.getId()));
        }
    }

    public void assertEquals(IImageSubRect expected, IImageSubRect actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getArea(), actual.getArea());
    }

    public ImageDefinition createImageDef(String id) {
        return new ImageDefinition(id, Dim.of(1, 1));
    }

}
