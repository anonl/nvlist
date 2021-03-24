package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lwjgl.system.Platform;

import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.file.TempFileProvider;

public abstract class FfmpegEncoderTest {

    protected static final IEncodedResource EMPTY_RESOURCE = EncodedResource.fromBytes(new byte[0]);

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    protected TempFileProvider  tempFileProvider;

    @Before
    public final void setUp() {
        tempFileProvider = new TempFileProvider(tempFolder.getRoot());
    }

    @Test
    public void testIsAvailable() {
        FfmpegEncoder encoder = newEncoder();

        encoder.setProgram(getDummyExecutable());
        Assert.assertTrue(encoder.isAvailable());

        encoder.setProgram("doesntexist");
        Assert.assertFalse(encoder.isAvailable());
    }

    protected abstract FfmpegEncoder newEncoder();

    protected final String getDummyExecutable() {
        Platform platform = Platform.get();
        switch (platform) {
        case WINDOWS:
            return new File("src/test/resources/nop.exe").getAbsolutePath();
        case LINUX:
            return new File("/bin/true").getAbsolutePath();
        default:
            throw new AssumptionViolatedException("Unsupported platform: " + platform);
        }
    }

    /**
     * @param expected Null values in the array cause the equality comparison to be skipped.
     */
    protected final void assertProcessArgs(List<String> expected, List<String> actual) {
        Assert.assertEquals(expected.size(), actual.size());
        for (int n = 0; n < expected.size(); n++) {
            if (expected.get(n) != null) {
                Assert.assertEquals(expected.get(n), actual.get(n));
            }
        }
    }

}
