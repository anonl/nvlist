package nl.weeaboo.vn.impl.save;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IProgressListener;

public final class ProgressInputStream extends FilterInputStream {

    private final int updateBytes;
    private final long length;
    private final IProgressListener pl;

    private long lastReportedPos;
    private long pos;

    private ProgressInputStream(InputStream in, int updateBytes, long length, IProgressListener pl) {
        super(in);

        if (length < 0) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }

        this.updateBytes = Checks.checkRange(updateBytes, "updateBytes", 1);
        this.length = length;
        this.pl = Checks.checkNotNull(pl);
    }

    /**
     * @see #wrap(InputStream, int, long, IProgressListener)
     */
    public static InputStream wrap(InputStream in, long length, IProgressListener pl) {
        return wrap(in, 2048, length, pl);
    }

    /**
     * Wraps the given input stream in such a way that the progress listener is called every {@code updateBytes}. If the
     * progress listener is {@code null}, the input stream is returned unmodified.
     *
     * @param pl May be null.
     */
    public static InputStream wrap(InputStream in, int updateBytes, long length, IProgressListener pl) {
        if (pl != null) {
            return new ProgressInputStream(in, updateBytes, length, pl);
        } else {
            return in;
        }
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        n = in.skip(n);

        if (n > 0) {
            pos += n;

            if (pos - lastReportedPos >= updateBytes || pos == length) {
                lastReportedPos = pos;
                pl.onProgressChanged((float)(pos / (double)length));
            }
        }

        return n;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = super.read(b, off, len);

        if (bytesRead > 0) {
            pos += bytesRead;
            if (pos - lastReportedPos >= updateBytes || pos == length) {
                lastReportedPos = pos;
                pl.onProgressChanged((float)(pos / (double)length));
            }
        }

        return bytesRead;
    }

    @Override
    public synchronized int read() throws IOException {
        int value = super.read();

        if (value >= 0) {
            pos++;
            if (pos - lastReportedPos >= updateBytes || pos == length) {
                lastReportedPos = pos;
                pl.onProgressChanged((float)(pos / (double)length));
            }
        }

        return value;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public synchronized void mark(int readlimit) {
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

}
