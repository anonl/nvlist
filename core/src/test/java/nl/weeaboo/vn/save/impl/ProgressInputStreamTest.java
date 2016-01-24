package nl.weeaboo.vn.save.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.core.impl.TestProgressListener;

public class ProgressInputStreamTest {

    private static final float EPSILON = .001f;

    private TestProgressListener listener;

    private byte[] data;
    private InputStream inputStream;

    @Before
    public void before() {
        listener = new TestProgressListener();

        data = new byte[10000];

        /*
         * Fill with random data so we can detect if the data is passed through the progress wrapper
         * unmolested
         */
        Random random = new Random(12345);
        random.nextBytes(data);

        inputStream = ProgressInputStream.wrap(new ByteArrayInputStream(data), data.length, listener);
    }

    @Test
    public void wrapWithNullListener() {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        // If there's no listener attached, simply returns the original stream
        Assert.assertSame(bin, ProgressInputStream.wrap(bin, data.length, null));
    }

    @Test
    public void bulkRead() throws IOException {
        byte[] read = new byte[data.length];
        Assert.assertEquals(data.length, inputStream.read(read));

        // Check that the bytes read match the original data
        Assert.assertArrayEquals(data, read);

        /*
         * Because we get data from the underlying stream in a single bulk read, we only get one event. The
         * alternative would be to sacrifice performance by splitting up the requested bulk read into smaller
         * chunks in order to report progress more accurately. Performance is more important here.
         */
        Assert.assertEquals(1, listener.consumeEventCount());

        // Trying to read from the empty stream doesn't result in any additional events
        Assert.assertEquals(-1, inputStream.read(new byte[1]));
        Assert.assertEquals(0, listener.consumeEventCount());
        Assert.assertEquals(1.0f, listener.getLastProgress(), EPSILON);
    }

    @Test
    public void skip() throws IOException {
        // skip works just like a bulk read
        Assert.assertEquals(data.length, inputStream.skip(data.length));

        // 1 call to skip results in 1 event at most
        Assert.assertEquals(1, listener.consumeEventCount());
        Assert.assertEquals(1.0f, listener.getLastProgress(), EPSILON);

        // Nothing further to skip
        Assert.assertEquals(0, inputStream.skip(1));
        Assert.assertEquals(0, listener.consumeEventCount());
    }

    @Test
    public void singleByteRead() throws IOException {
        byte[] read = new byte[data.length];
        for (int n = 0; n < read.length; n++) {
            int r = inputStream.read();
            if (r < 0) {
                Assert.fail("Unexpected end of stream: " + n);
            }
            read[n] = (byte)r;
        }

        // Check that the bytes read match the original data
        Assert.assertArrayEquals(data, read);

        // Expect ceil(10_000 / 2048.0) = 5 events
        Assert.assertEquals(5, listener.consumeEventCount());
        Assert.assertEquals(1.0f, listener.getLastProgress(), EPSILON);
    }

    /** Throws an exception if length < 0 */
    @Test(expected = IllegalArgumentException.class)
    public void negativeLengthGiven() {
        ProgressInputStream.wrap(new ByteArrayInputStream(data), -1, listener);
    }

}
