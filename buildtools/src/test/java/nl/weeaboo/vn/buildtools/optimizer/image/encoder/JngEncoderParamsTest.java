package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import org.junit.Assert;
import org.junit.Test;

public class JngEncoderParamsTest {

    @Test
    public void testDefaultValues() {
        JngEncoderParams params = new JngEncoderParams();
        Assert.assertEquals(.90, params.getJpegQuality(), .001);
        Assert.assertEquals(.95, params.getJpegAlphaQuality(), .001);
        Assert.assertEquals(true, params.isAllowLossyAlpha());
    }

    @Test
    public void testSetters() {
        JngEncoderParams params = new JngEncoderParams();

        Assert.assertThrows(IllegalArgumentException.class, () -> params.setJpegQuality(-.001f));
        Assert.assertThrows(IllegalArgumentException.class, () -> params.setJpegQuality(1.001f));
        params.setJpegQuality(.1f);
        Assert.assertEquals(.1, params.getJpegQuality(), .001);

        Assert.assertThrows(IllegalArgumentException.class, () -> params.setJpegAlphaQuality(-.001f));
        Assert.assertThrows(IllegalArgumentException.class, () -> params.setJpegAlphaQuality(1.001f));
        params.setJpegAlphaQuality(.2f);
        Assert.assertEquals(.2, params.getJpegAlphaQuality(), .001);

        params.setAllowLossyAlpha(false);
        Assert.assertEquals(false, params.isAllowLossyAlpha());
    }

}