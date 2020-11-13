package nl.weeaboo.vn.buildtools.optimizer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.file.ITempFileProvider;
import nl.weeaboo.vn.buildtools.file.TempFileProvider;

public final class FfmpegEncoderTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder(new File("build/temp"));

    private TestFfmpegEncoder encoder;

    @Before
    public void before() {
        encoder = new TestFfmpegEncoder(new TempFileProvider(tempFolder.getRoot()));
    }

    @Test
    public void testCommandLineArgs() throws IOException {
        IEncodedResource resource = EncodedResource.fromBytes(new byte[0]);
        encoder.encode(resource);

        encoder.assertProcessArgs("ffmpeg", "-i", null, "-f", "myformat", "-y", null);
    }

    private static final class TestFfmpegEncoder extends FfmpegEncoder {

        private ImmutableList<String> lastProcessArgs = ImmutableList.of();

        public TestFfmpegEncoder(ITempFileProvider tempFileProvider) {
            super(tempFileProvider);
        }

        @Override
        protected void runProcess(List<String> command) {
            lastProcessArgs = ImmutableList.copyOf(command);
        }

        /**
         * @param expected Null values in the array cause the equality comparison to be skipped.
         */
        public void assertProcessArgs(String... expected) {
            Assert.assertEquals(expected.length, lastProcessArgs.size());
            for (int n = 0; n < expected.length; n++) {
                if (expected[n] != null) {
                    Assert.assertEquals(expected[n], lastProcessArgs.get(n));
                }
            }
        }

        @Override
        protected List<String> getCodecArgs() {
            return ImmutableList.of();
        }

        @Override
        protected String getFileFormat() {
            return "myformat";
        }

    }
}
