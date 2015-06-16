package nl.weeaboo.io;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public final class RandomAccessUtil {
    
    public static IRandomAccessFile wrap(RandomAccessFile file) {
        return new RandomAccessFileWrapper(file);
    }
    
    public static IRandomAccessFile wrap(FileInputStream fin) {
        return wrap(fin, 0, -1, null);
    }
    public static IRandomAccessFile wrap(FileInputStream fin, long off, long len) {
        return wrap(fin, off, len, null);
    }
    public static IRandomAccessFile wrap(FileInputStream fin, long off, long len, FileOutputStream fout) {  
        return new FileStreamWrapper(fin, off, len, fout);
    }

    public static IRandomAccessFile wrap(byte[] bytes, int off, int len) {
        return new RandomAccessBufferWrapper(bytes, off, len);
    }
    
    //Inner Classes 
    private static final class RandomAccessFileWrapper implements IRandomAccessFile {
        
        private final RandomAccessFile file;

        RandomAccessFileWrapper(RandomAccessFile file) {
            this.file = file;
        }
        
        @Override
        public synchronized void close() throws IOException {
            file.close();
        }
        
        @Override
        public synchronized long skip(long n) throws IOException {
            return file.skipBytes((int)Math.min(Integer.MAX_VALUE, n));
        }

        @Override
        public synchronized int read() throws IOException {
            return file.read();
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            return file.read(b, off, len);
        }

        @Override
        public synchronized void write(int b) throws IOException {
            file.write(b);
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            file.write(b, off, len);
        }

        @Override
        public synchronized long pos() throws IOException {
            return file.getFilePointer();
        }

        @Override
        public synchronized void seek(long pos) throws IOException {
            file.seek(pos);
        }

        @Override
        public synchronized long length() throws IOException {
            return file.length();
        }

        @Override
        public synchronized InputStream getInputStream() throws IOException {
            return getInputStream(0, length());
        }

        @Override
        public synchronized InputStream getInputStream(long offset, long length) throws IOException {
            return new RandomAccessInputStream(this, offset, length);
        }
        
    }

    private static final class FileStreamWrapper implements IRandomAccessFile {
        
        private final FileInputStream fin;
        private final FileChannel finc;
        private final long inOff, inLen;
        private final FileOutputStream fout;
        private final FileChannel foutc;

        private long inPos;
        
        FileStreamWrapper(FileInputStream fin, long off, long len, FileOutputStream fout) {
            this.fin = fin;
            this.finc = fin.getChannel();
            this.inOff = off;
            this.inLen = len;
            this.fout = fout;
            this.foutc = (fout != null ? fout.getChannel() : null);
            
            inPos = 0;
        }
        
        @Override
        public synchronized void close() throws IOException {
            try {
                if (fin != null) fin.close();
            } finally {
                if (fout != null) fout.close();
            }
        }
        
        private long remaining() throws IOException {
            return length() - inPos;
        }
        
        @Override
        public synchronized long skip(long n) throws IOException {
            long skipped = Math.min(n, remaining());        
            inPos += skipped;
            return skipped;
        }

        @Override
        public synchronized int read() throws IOException {
            if (inPos < 0 || inPos >= inLen) return -1;

            if (finc.position() != inOff+inPos) {
                finc.position(inOff+inPos);
            }
            inPos++;
            return fin.read();
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            long r = remaining();
            if (r <= 0) {
                return (len == 0 ? 0 : -1);
            }
            len = Math.min(len, (int)Math.min(Integer.MAX_VALUE, r));

            if (finc.position() != inOff+inPos) {
                finc.position(inOff+inPos);
            }
            len = fin.read(b, off, len);
            if (len > 0) inPos += len;
            return len;
        }

        @Override
        public synchronized void write(int b) throws IOException {
            if (fout == null) throw new IOException("FileOutputStream not supplied, can't write data");
                
            foutc.position(inOff+inPos);
            fout.write(b);
            finc.position(foutc.position());
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            if (fout == null) throw new IOException("FileOutputStream not supplied, can't write data");

            foutc.position(inOff+inPos);
            fout.write(b, off, len);
            finc.position(foutc.position());
        }

        @Override
        public synchronized long pos() throws IOException {
            return inPos;
        }

        @Override
        public synchronized void seek(long pos) throws IOException {
            inPos = pos;
        }

        @Override
        public synchronized long length() throws IOException {
            return (inLen >= 0 ? inLen : finc.size()-inOff);
        }

        @Override
        public synchronized InputStream getInputStream() throws IOException {
            return getInputStream(0, length());
        }

        @Override
        public synchronized InputStream getInputStream(long offset, long length) throws IOException {
            return new RandomAccessInputStream(this, offset, length);
        }
        
    }
    
    private static final class RandomAccessBufferWrapper implements IRandomAccessFile {
        
        private final ByteBuffer buf;

        RandomAccessBufferWrapper(byte[] bytes, int off, int len) {
            buf = ByteBuffer.wrap(bytes, off, len);
        }
        
        @Override
        public void close() throws IOException {
            // Nothing to do
        }
        
        @Override
        public synchronized long skip(long n) throws IOException {
            int toSkip = (int)Math.min(buf.remaining(), n);
            buf.position(buf.position() + toSkip);
            return toSkip;
        }

        @Override
        public synchronized int read() throws IOException {
            return (buf.hasRemaining() ? buf.get() & 0xFF : -1);
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            
            int r = Math.min(buf.remaining(), len);     
            buf.get(b, off, r);
            return r;
        }

        @Override
        public synchronized void write(int b) throws IOException {
            if (!buf.hasRemaining()) {
                throw new EOFException("Buffer overflow");
            }
            if (buf.isReadOnly()) {
                throw new IOException("Unable to write to read-only buffer");
            }
            buf.put((byte)b);
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException {
            for (int n = 0; n < len; n++) {
                write(b[off + n]);
            }
        }

        @Override
        public synchronized long pos() throws IOException {
            return buf.position();
        }

        @Override
        public synchronized void seek(long pos) throws IOException {
            if (pos < 0 || pos > buf.limit()) {
                throw new IOException("Invalid position: " + pos);
            }
            buf.position((int)pos);
        }

        @Override
        public synchronized long length() throws IOException {
            return buf.limit();
        }

        @Override
        public synchronized InputStream getInputStream() throws IOException {
            return getInputStream(0, length());
        }

        @Override
        public synchronized InputStream getInputStream(long offset, long length) throws IOException {
            return new RandomAccessInputStream(this, offset, length);
        }
        
    }
}
