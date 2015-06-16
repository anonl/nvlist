package nl.weeaboo.filesystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class InMemoryFile {

	private final String path;
	
	private long modifiedTime;	
	private byte[] contents = new byte[0];
	
	// Number of open input/output streams to this file entry
	private int openInputStreams;
	private int openOutputStreams;
	
	public InMemoryFile(String path) {
		this.path = path;
	}

	public void delete() throws IOException {
		checkNotReading();
		checkNotWriting();
	}
	
	public InMemoryFile copy() throws IOException {
		return copy(path);
	}
	public InMemoryFile copy(String path) throws IOException {
		InMemoryFile copy = new InMemoryFile(path);
		synchronized (this) {
			checkNotWriting();
			copy.modifiedTime = modifiedTime;
			copy.contents = contents;
		}
		return copy;
	}
	
	private void checkNotReading() throws IOException {
		if (openInputStreams > 0) {
			throw new IOException("File is currently opened for reading: " + path);
		}		
	}
	
	private void checkNotWriting() throws IOException {
		if (openOutputStreams > 0) {
			throw new IOException("File is currently opened for writing: " + path);
		}		
	}
	
	public String getName() {
		return path;
	}
	
	public synchronized long getFileSize() {
		return contents.length;
	}
	
	public synchronized long getModifiedTime() {
		return modifiedTime;
	}
	
	public synchronized InputStream openInputStream() throws IOException {
		checkNotWriting();

		openInputStreams++;		
		return new ByteArrayInputStream(contents) {
			@Override
			public void close() throws IOException {
				synchronized (this) {
					openInputStreams--;
				}
				
				super.close();
			}
		};
	}
	
	public synchronized OutputStream openOutputStream() throws IOException {
		checkNotReading();
		checkNotWriting();
		
		openOutputStreams++;
		return new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				synchronized (this) {
					openOutputStreams--;
					setContents(toByteArray());
				}
				
				super.close();
			}
		};
	}
	
	synchronized void setContents(byte[] data) {
		contents = data;
		modifiedTime = System.currentTimeMillis();
	}
	
}
