package nl.weeaboo.io;

import java.io.IOException;
import java.io.InputStream;

final class RandomAccessInputStream extends InputStream {
	
	private final IRandomAccessFile file;
	private final long offset;
	private final long length;

	private long read;
	private long mark;
	
	public RandomAccessInputStream(IRandomAccessFile file, long offset, long length) {
		this.file = file;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public void close() {
		//Nothing to do, this is just a view on the file
	}
	
	@Override
	public synchronized long skip(long s) throws IOException {
    	long skipped = Math.min(s, length - read);    	
    	read += skipped;
    	return skipped;
	}
	
	@Override
    public synchronized int read() throws IOException {
    	if (read >= length) return -1;

    	int b = -1;
    	synchronized (file) { 
	    	if (file.pos() != offset + read) {
	    		file.seek(offset + read);
	    	}
	    	b = file.read();
    	}
    	read++;
    	return b;
    }
    
	@Override
    public synchronized int read(byte[] out, int off, int len) throws IOException {
    	if (read >= length) {
    	    return (len == 0 ? 0 : -1);
    	}
    	
    	len = (int)Math.min(len, length - read);
    	
    	int r = -1;
    	synchronized (file) { 
	    	if (file.pos() != offset + read) {
	    		file.seek(offset + read);
	    	}
	    	r = file.read(out, off, len);
    	}
    	
    	if (r > 0) {
    		read += r;
    	}
    	return r;	    	
    }
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	@Override
	public synchronized void mark(int readLimit) {
		mark = read;
	}
	
	@Override
	public synchronized void reset() throws IOException {
		read = mark;
	}
    
}