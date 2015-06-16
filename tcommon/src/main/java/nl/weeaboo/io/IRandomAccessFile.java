package nl.weeaboo.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IRandomAccessFile {

	/** @see InputStream#close() */
	public void close() throws IOException;

	/** @see InputStream#skip(long) */
	public long skip(long s) throws IOException;
	
	/** @see InputStream#read() */
	public int read() throws IOException;

	/** @see InputStream#read(byte[], int, int) */
	public int read(byte[] b, int off, int len) throws IOException;

	/** @see OutputStream#write(int) */
	public void write(int b) throws IOException;

	/** @see OutputStream#write(byte[], int, int) */
	public void write(byte[] b, int off, int len) throws IOException;

	public long pos() throws IOException;
	public void seek(long pos) throws IOException;
	public long length() throws IOException;

	public InputStream getInputStream() throws IOException;
	public InputStream getInputStream(long offset, long length) throws IOException;
	
}
