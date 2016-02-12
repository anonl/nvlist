package nl.weeaboo.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtil {

	public enum Compression {
		NONE,
		DEFLATE,
		NONE_BAD_CRC
	}

	private ZipUtil() {
	}

	public static void writeFolderEntry(ZipOutputStream zout, String relpath) throws IOException {
		if (relpath.length() > 0 && !relpath.endsWith("/")) {
			relpath += "/";
		}

		ZipEntry entry = new ZipEntry(relpath);
		zout.putNextEntry(entry);
		zout.closeEntry();
	}

    public static void add(ZipOutputStream zout, String relpath, File file, Compression c)
            throws IOException {

		if (file.isDirectory()) {
		    writeFolderEntry(zout, relpath);
		} else {
			FileInputStream fin = new FileInputStream(file);
			try {
			    writeFileEntry(zout, relpath, fin, file.length(), c);
			} finally {
				fin.close();
			}
		}
	}

	public static void writeFileEntry(ZipOutputStream zout, String relpath, byte[] b, int off, int len,
			Compression c) throws IOException
	{

		ByteArrayInputStream bin = new ByteArrayInputStream(b, off, len);
		try {
		    writeFileEntry(zout, relpath, bin, len, c);
		} finally {
			bin.close();
		}
	}

   	public static void writeFileEntry(ZipOutputStream zout, String relpath,
   	 		InputStream in, long size, Compression c) throws IOException
   	{
   		final int readBufSize = (32 << 10);
   		final int smallFileThreshold = (1 << 20);

   		if (size == 0 || c == null) {
   		    c = Compression.NONE;
   		}

   		//Create ZIP Entry
		ZipEntry entry = new ZipEntry(relpath);
		entry.setMethod(c == Compression.DEFLATE ? ZipEntry.DEFLATED : ZipEntry.STORED);
		entry.setSize(size);

		File tempF = null;
		OutputStream contentOut;
		OutputStream tempOut = null;
        if (c == Compression.NONE) {
            /*
             * Java requires a CRC before writing uncompressed ZIP entries. To calculate the CRC we have to
             * read the InputStream, store the data we've read in some way, then write that data to the zip
             * stream.
             */
        	if (size >= 0 && size <= smallFileThreshold) {
        		tempOut = new ByteArrayOutputStream(readBufSize);
        	} else {
        		tempF = File.createTempFile("zipchunk", ".zip");
        		tempOut = new FileOutputStream(tempF);
        	}
        	contentOut = tempOut;
        } else {
        	if (c == Compression.NONE_BAD_CRC) {
        		entry.setCrc(0);
        	}
        	zout.putNextEntry(entry);
        	contentOut = zout;
        }

        try {
            CRC32 crc = new CRC32();

	        if (size > 0) {
				long read = 0;
				byte buf[] = new byte[readBufSize];
				while (read < size) {
					int r = in.read(buf);
					if (r < 0) {
                        throw new EOFException("Unexpected end of file, read=" + read + " expected=" + size);
					}

					read += r;
			        crc.update(buf, 0, r);
					contentOut.write(buf, 0, r);
				}
			}

	        entry.setCrc(crc.getValue());
        } finally {
            if (tempOut != null) {
                tempOut.close();
            }

        	if (tempF != null && !tempF.delete()) {
        		throw new IOException("Unable to delete temp file: " + tempF);
        	}
        }

        //If we've written to a temporary file/buffer, copy the data to the zip stream
        if (tempOut != null) {
            zout.putNextEntry(entry);

            if (tempOut instanceof ByteArrayOutputStream) {
                ((ByteArrayOutputStream)tempOut).writeTo(zout);
            } else {
                InputStream tempIn = new FileInputStream(tempF);
                try {
                    byte[] buf = new byte[readBufSize];
                    while (true) {
                        int r = tempIn.read(buf, 0, readBufSize);
                        if (r < 0) {
                            break;
                        }
                        zout.write(buf, 0, r);
                    }
                } finally {
                    tempIn.close();
                }
            }
        }

		zout.closeEntry();
	}

}
