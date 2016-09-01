package nl.weeaboo.vn.image.impl.desc;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.CoreTestUtil;
import nl.weeaboo.vn.image.desc.GLScaleFilter;
import nl.weeaboo.vn.image.desc.GLTilingMode;
import nl.weeaboo.vn.image.desc.IImageSubRect;

public class ImageDefinitionIOTest {

    /** Simple definition with no subrects */
    @Test
    public void minimal() throws IOException {
        ImageDefinition def = Iterables.getOnlyElement(load("minimal.json"));
        Assert.assertEquals("minimal", def.getFile().toString());
        Assert.assertEquals(Dim.of(16, 32), def.getSize());
        Assert.assertEquals(GLScaleFilter.DEFAULT, def.getMinifyFilter());
        Assert.assertEquals(GLScaleFilter.DEFAULT, def.getMagnifyFilter());
        Assert.assertEquals(GLTilingMode.DEFAULT, def.getTilingModeX());
        Assert.assertEquals(GLTilingMode.DEFAULT, def.getTilingModeY());
    }

    @Test
    public void allattrs() throws IOException {
        ImageDefinition def = Iterables.getOnlyElement(load("allattrs.json"));
        Assert.assertEquals("allattrs", def.getFile().toString());
        Assert.assertEquals(Dim.of(64, 128), def.getSize());
        Assert.assertEquals(GLScaleFilter.NEAREST, def.getMinifyFilter());
        Assert.assertEquals(GLScaleFilter.LINEAR, def.getMagnifyFilter());
        Assert.assertEquals(GLTilingMode.CLAMP, def.getTilingModeX());
        Assert.assertEquals(GLTilingMode.REPEAT, def.getTilingModeY());
    }

    @Test
    public void subrectMinimal() throws IOException {
        ImageDefinition def = Iterables.getOnlyElement(load("subrects1.json"));
        IImageSubRect subRect = Iterables.getOnlyElement(def.getSubRects());
        Assert.assertEquals("r1", subRect.getId());
        CoreTestUtil.assertEquals(1, 2, 3, 4, subRect.getRect().toRect2D());
    }

    private Collection<ImageDefinition> load(String path) throws IOException {
        String content = Resources.toString(getClass().getResource("/imagedesc/" + path), Charsets.UTF_8);
        return ImageDefinitionIO.deserialize(content);
    }

}
