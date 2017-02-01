package nl.weeaboo.vn.impl.save;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.impl.core.TestProgressListener;
import nl.weeaboo.vn.impl.save.ProgressOutputStream;

public class ProgressOutputStreamTest {

    private static final float EPSILON = .001f;
    private static final int BYTES_PER_EVENT = 100;

    private TestProgressListener listener;
    private byte[] data;
    private OutputStream outputStream;
    private ByteArrayOutputStream sink;

    @Before
    public void before() {
        listener = new TestProgressListener();

        data = new byte[1234];

        /*
         * Fill with random data so we can detect if the data is passed through the progress wrapper
         * unmolested
         */
        Random random = new Random(54321);
        random.nextBytes(data);

        sink = new ByteArrayOutputStream();
        outputStream = ProgressOutputStream.wrap(sink, BYTES_PER_EVENT, listener);
    }

    @Test
    public void wrapWithNullListener() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        // If there's no listener attached, simply returns the original stream
        Assert.assertSame(bout, ProgressOutputStream.wrap(bout, null));
    }

    @Test
    public void bulkWrite() throws IOException {
        // Now write the test data to the stream
        outputStream.write(data);

        // Check that the bytes written match the original data
        Assert.assertArrayEquals(data, sink.toByteArray());

        // We get max 1 event per write operation
        Assert.assertEquals(1, listener.consumeEventCount());
        // Progress events pass the number of bytes written as the value of 'progress'
        Assert.assertEquals(1200f, listener.getLastProgress(), EPSILON);
    }

    /** Writing zero bytes should never trigger an event */
    @Test
    public void zeroByteWrites() throws IOException {
        outputStream.write(new byte[BYTES_PER_EVENT]);
        Assert.assertEquals(1, listener.consumeEventCount());
        outputStream.write(new byte[0]);
        Assert.assertEquals(0, listener.consumeEventCount());
    }

    @Test
    public void singleByteWrite() throws IOException {
        for (int n = 0; n < data.length; n++) {
            outputStream.write(data[n]);
        }

        // Check that the bytes written match the original data
        Assert.assertArrayEquals(data, sink.toByteArray());

        // Expect 1234 / 100 = 12 events
        Assert.assertEquals(12, listener.consumeEventCount());
        // Progress events pass the number of bytes written as the value of 'progress'
        Assert.assertEquals(1200f, listener.getLastProgress(), EPSILON);
    }

}
