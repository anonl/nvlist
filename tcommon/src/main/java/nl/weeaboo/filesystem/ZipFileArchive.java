package nl.weeaboo.filesystem;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.io.IRandomAccessFile;
import nl.weeaboo.io.StreamUtil;

/** Only supports ZIP files with UTF-8 encoded filenames */
public class ZipFileArchive extends AbstractFileArchive {

    private static final int READ_BUF = 4096;
    
    @Override
    protected ArchiveFileRecord[] initRecords(IRandomAccessFile rfile) throws IOException {
        long centralDirPos = findCentralDir(rfile);
        if (centralDirPos < 0) {
            throw new IOException("ZIP central directory not found");
        }
        
        InputStream in;
        
        ByteBuffer centralDirBuf = ByteBuffer.allocate(20);
        centralDirBuf.order(ByteOrder.LITTLE_ENDIAN);

        in = new BufferedInputStream(rfile.getInputStream(centralDirPos, rfile.length() - centralDirPos), READ_BUF);
        try {
            fill(in, centralDirBuf);
        } finally {
            in.close();
        }        

        centralDirBuf.getInt();   // End of central directory magic
        centralDirBuf.getShort(); // Disk number
        centralDirBuf.getShort(); // Central directory disk number
        centralDirBuf.getShort(); // Number of central directory records on this disk
        
        int fileCount = (centralDirBuf.getShort() & 0xFFFF);
        centralDirBuf.getInt(); // Central directory size
        long offset = (centralDirBuf.getInt() & 0xFFFFFFFFL);
        
        ArchiveFileRecord[] records = new ArchiveFileRecord[fileCount];
        
        in = new BufferedInputStream(rfile.getInputStream(offset, rfile.length() - offset), READ_BUF);
        try {
            ByteBuffer buf = ByteBuffer.allocate(46);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            
            for (int n = 0; n < fileCount; n++) {
                fill(in, buf);
                
                buf.getInt();   // Central directory entry magic
                buf.getShort(); // Version made by
                buf.getShort(); // Version needed to extract
                buf.getShort(); // General purpose bit flag

                int compressionMethod = buf.getShort();
                if (compressionMethod != ZipEntry.STORED && compressionMethod != ZipEntry.DEFLATED) {
                    throw new IOException("Unsupported compression method (" + compressionMethod + ")");
                }
    
                int dosDateTime = buf.getInt();
                buf.getInt();   // CRC
                long compressedLength   = (buf.getInt() & 0xFFFFFFFFL);
                long uncompressedLength = (buf.getInt() & 0xFFFFFFFFL);
                int  filenameLength     = (buf.getShort() & 0xFFFF);
                int  extraFieldLength   = (buf.getShort() & 0xFFFF);
                int  commentLength      = (buf.getShort() & 0xFFFF);
                buf.getShort(); // Disk number
                buf.getShort(); // Internal file attributes
                buf.getInt();   // External file attributes                
                long headerOffset       = (buf.getInt() & 0xFFFFFFFFL);
                
                byte[] filenameBytes = new byte[filenameLength];
                StreamUtil.readFully(in, filenameBytes, 0, filenameLength);
                String filename = StringUtil.fromUTF8(filenameBytes);
                
                StreamUtil.forceSkip(in, extraFieldLength + commentLength);
                                
                records[n] = new ArchiveFileRecord(filename, headerOffset, compressedLength, uncompressedLength,
                        (byte) compressionMethod, dosDateTime);
            }
        } finally {
            in.close();
        }
        
        return records;
    }
    
    private static void fill(InputStream in, ByteBuffer buf) throws IOException {
        buf.rewind();
        StreamUtil.readFully(in, buf.array(), buf.arrayOffset(), buf.limit());
    }

    public static long findCentralDir(IRandomAccessFile file) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(READ_BUF);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        final int centralDirMagic = 0x06054b50;        

        long pos = file.length();
        do {
            // Need 3 extra bytes in case magic number falls on read boundary
            pos = Math.max(0, pos - (buf.limit() - 3));
            file.seek(pos);

            // Note: do not use fill(), it throws an exception if EOF is reached.
            buf.limit(buf.capacity());
            while (buf.remaining() > 0) {
                int r = file.read(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining());
                if (r < 0) {
                    buf.limit(buf.position());
                    break;
                }
                buf.position(buf.position() + r);
            }
            buf.rewind();
            
            int tlim = buf.limit() - 4;
            int t = tlim;
            while (t >= 0 && t <= tlim) {
                int read = buf.getInt(t);
                if (read == centralDirMagic) {
                    return pos + t;
                }
                t--;
            }
        } while (pos > 0);
        
        return -1;
    }
    
    @Override
    protected InputStream openInputStreamImpl(String path) throws IOException {
        ArchiveFileRecord file = getFileImpl(path);

        long fileOffset = getFileOffset(file.getHeaderOffset());
        InputStream in = rfile.getInputStream(fileOffset, file.getCompressedLength());
        if (file.getCompression() == ZipEntry.DEFLATED) {
            in = new CompressedEntryInputStream(in);
        }
        return in;
    }
    
    @Override
    protected long getFileOffset(long headerOffset) throws IOException {
        long fileOffset;
        
        byte[] headerBytes = new byte[30];
        InputStream headerIn = rfile.getInputStream(headerOffset, headerBytes.length);
        try {
            ByteBuffer header = ByteBuffer.wrap(headerBytes);
            header.order(ByteOrder.LITTLE_ENDIAN);
            while (header.hasRemaining()) {
                int read = headerIn.read(header.array(), header.position(), header.remaining());
                if (read < 0) break;
                header.position(header.position() + read);
            }
            
            header.position(26);
            int filenameLength = header.getShort() & 0xFFFF;
            int extraLength = header.getShort() & 0xFFFF;
            
            fileOffset = headerOffset + 30 + filenameLength + extraLength;
        } finally {
            headerIn.close();
        }
        
        return fileOffset;
    }    
    
    private static class CompressedEntryInputStream extends InflaterInputStream {
        
        private boolean eof;
        
        public CompressedEntryInputStream(InputStream in) {
            super(in, new Inflater(true));
        }

        //Provide 1-byte padding at end of compressed data (required when using inflator with nowrap=true)
        @Override
        protected synchronized void fill() throws IOException {
            if (eof) {
                throw new EOFException();
            }
            
            len = in.read(buf);
            if (len < 0) {
                eof = true;
                len = 1;
                buf[0] = 0;
            }
            inf.setInput(buf, 0, len);
        }
        
    }

}
