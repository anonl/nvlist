package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class JngInputUtil {

    private JngInputUtil() {
    }

    static void forceSkip(DataInput in, int toSkip) throws IOException {
        int skipped = 0;
        while (skipped < toSkip) {
            int s = in.skipBytes(toSkip - skipped);
            if (s <= 0) {
                break;
            }
            skipped += s;
        }
        for (int n = skipped; n < toSkip; n++) {
            in.readByte();
        }
    }

    static DataInput toDataInput(InputStream in) {
        if (in instanceof DataInput) {
            return (DataInput)in;
        } else {
            return new DataInputStream(in);
        }
    }

    static byte[] concatChunks(Iterable<byte[]> chunksList) {
        int bytesCount = 0;
        for (byte[] chunk : chunksList) {
            bytesCount += chunk.length;
        }

        return concatChunks(chunksList, bytesCount);
    }

    static byte[] concatChunks(Iterable<byte[]> chunksList, int bytesCount) {
        byte[] dst = new byte[bytesCount];

        int dstOffset = 0;
        for (byte[] chunk : chunksList) {
            System.arraycopy(chunk, 0, dst, dstOffset, chunk.length);
            dstOffset += chunk.length;
        }
        return dst;
    }

    public static boolean startsWith(byte[] bytes, int offset, int length, byte[] pattern) {
        // Check if the data starts with the JNG magic
        if (length < pattern.length) {
            return false;
        }

        for (int n = 0; n < pattern.length; n++) {
            if (bytes[offset + n] != pattern[n]) {
                return false;
            }
        }
        return true;
    }

    public static String toByteString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x ", b & 0xFF));
        }
        return sb.toString();
    }
}
