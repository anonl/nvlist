package nl.weeaboo.io;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class StreamUtil {

    private StreamUtil() {        
    }
    
	public static int skipBOM(byte b[], int off, int len) {
		if (len >= 2) {
			if ((b[off] == (byte)0xFF && b[off+1] == (byte)0xFE)
				|| (b[off] == (byte)0xFE && b[off+1] == (byte)0xFF))
			{
				//Skip UTF-16 BOM
				off += 2;
			} else if (len >= 3 && b[off] == (byte)0xEF
					&& b[off+1] == (byte)0xBB && b[off+2] == (byte)0xBF)
			{
				//Skip UTF-8 BOM
				off += 3;
			}
		}
		return off;
	}
	
    public static byte[] readBytes(InputStream in) throws IOException {
        final int readBufSize = 4096;
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream(readBufSize);
        try {       
            int r;
            byte buf[] = new byte[readBufSize];
            while ((r = in.read(buf)) > 0) {
                bout.write(buf, 0, r);
            }
        } finally {
            bout.close();
        }        
        return bout.toByteArray();
    }
    
    public static void readFully(InputStream in, byte[] dst, int dstOff, int dstLen) throws IOException {
        int read = 0;
        while (read < dstLen) {
            int r = in.read(dst, dstOff + read, dstLen - read);
            if (r < 0) {
                throw new EOFException();
            }
            read += r;
        }
    }
	
    public static void forceSkip(InputStream in, long toSkip) throws IOException {
        long skipped = 0;
        while (skipped < toSkip) {
            long s = in.skip(toSkip - skipped);
            if (s <= 0) {
                break;
            }
            skipped += s;
        }        
        for (long n = skipped; n < toSkip; n++) {
            in.read();
        }
    }
    
}
