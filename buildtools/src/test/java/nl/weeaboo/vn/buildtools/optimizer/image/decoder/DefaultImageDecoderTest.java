package nl.weeaboo.vn.buildtools.optimizer.image.decoder;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.google.common.io.Resources;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

public class DefaultImageDecoderTest {

    private final DefaultImageDecoder decoder = new DefaultImageDecoder();

    @Before
    public void before() {
        GdxNativesLoader.load();
    }

    @Test
    public void testDecode() throws IOException {
        byte[] imageBytes = Resources.toByteArray(getClass().getResource("../a.png"));
        ImageDefinition def = new ImageDefinition("a.png", Dim.of(640, 360));
        ImageWithDef decoded = decoder.decode(new EncodedImage(EncodedResource.fromBytes(imageBytes), def));
        try {
            Assert.assertEquals(def, decoded.getDef());
            Pixmap pixmap = decoded.getPixmap();
            Assert.assertEquals(640, pixmap.getWidth());
            Assert.assertEquals(360, pixmap.getHeight());
        } finally {
            decoded.dispose();
        }
    }

}
