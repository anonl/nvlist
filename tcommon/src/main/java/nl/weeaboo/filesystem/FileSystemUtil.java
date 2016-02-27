package nl.weeaboo.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.io.StreamUtil;

public final class FileSystemUtil {

	private FileSystemUtil() {
	}

    public static byte[] readBytes(IFileSystem fs, String path) throws IOException {
        byte[] bytes;
        InputStream in = fs.openInputStream(path);
        try {
            bytes = StreamUtil.readBytes(in);
        } finally {
            in.close();
        }
        return bytes;
    }

    public static String readString(IFileSystem fs, String path) throws IOException {
        byte[] bytes = readBytes(fs, path);
        int skip = StreamUtil.skipBOM(bytes, 0, bytes.length);
        return StringUtil.fromUTF8(bytes, skip, bytes.length - skip);
    }

    public static void writeString(IWritableFileSystem fs, String path, String content) throws IOException {
        writeBytes(fs, path, StringUtil.toUTF8(content));
    }

    public static void writeBytes(IWritableFileSystem fs, String path, byte[] content) throws IOException {
        OutputStream out = fs.openOutputStream(path, false);
        try {
            out.write(content);
        } finally {
            out.close();
        }
    }

    public static Collection<String> withoutPathPrefix(Collection<String> paths, String prefix) {
		final int pL = prefix.length();

		Collection<String> result = new ArrayList<String>(paths.size());
		for (String path : paths) {
            if (path.length() > pL && path.charAt(pL) == '/') {
				//Path without pathPrefix would otherwise start with a '/'
				result.add(path.substring(pL+1));
			} else {
				result.add(path.substring(pL));
			}
		}
		return result;
	}

	public static Comparator<String> getFilenameComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				boolean aDir = a.endsWith("/");
				boolean bDir = b.endsWith("/");
				if (aDir && !bDir) return -1;
				if (!aDir && bDir) return 1;

				return a.compareToIgnoreCase(b);
			}
		};
	}

}
