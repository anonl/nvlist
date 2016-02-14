package nl.weeaboo.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import nl.weeaboo.io.IRandomAccessFile;
import nl.weeaboo.io.RandomAccessUtil;

public abstract class AbstractFileArchive extends AbstractFileSystem implements IFileArchive {

    private static final RecordPathComparator pathComparator = new RecordPathComparator();

	protected File file;
	protected IRandomAccessFile rfile;
	protected ArchiveFileRecord records[];

	public AbstractFileArchive() {
	}

	//Functions
    @Override
	public void open(File f) throws IOException {
		file = f;

		try {
            @SuppressWarnings("resource")
            IRandomAccessFile rfile = RandomAccessUtil.wrap(new RandomAccessFile(f, "r"));
            open(rfile);
		} catch (RuntimeException re) {
			throw re;
		} catch (IOException ioe) {
			throw ioe;
		}
	}

	@Override
	public void open(IRandomAccessFile f) throws IOException {
		rfile = f;

		try {
			records = initRecords(f);
            Arrays.sort(records, pathComparator);
		} catch (IOException ioe) {
			close();
			throw ioe;
		}
	}

	@Override
    protected void closeImpl() {
        if (rfile != null) {
            try {
                rfile.close();
            } catch (IOException e) {
                // Ignore
            }
            rfile = null;
        }
	}

	protected abstract ArchiveFileRecord[] initRecords(IRandomAccessFile f) throws IOException;

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    protected boolean getFileExistsImpl(String path) {
        try {
            return getFileImpl(path) != null;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    @Override
    protected long getFileSizeImpl(String path) throws IOException {
        return getFileImpl(path).getUncompressedLength();
    }

    @Override
    protected long getFileModifiedTimeImpl(String path) throws IOException {
        return getFileImpl(path).getModifiedTime();
    }

    public final ArchiveFileRecord getFile(String path) throws FileNotFoundException {
        return getFileImpl(normalizePath(path, false));
    }

    protected ArchiveFileRecord getFileImpl(String path) throws FileNotFoundException {
        int index = Arrays.binarySearch(records, path, pathComparator);
        if (index < 0) {
            throw new FileNotFoundException(path);
        }
        return records[index];
    }

	public long getFileOffset(String path) throws IOException {
		return getFileOffset(getFileImpl(path).getHeaderOffset());
	}

	protected abstract long getFileOffset(long headerOffset) throws IOException;

	@Override
	public Iterator<ArchiveFileRecord> iterator() {
		return new Iterator<ArchiveFileRecord>() {
			int t = 0;

			@Override
			public boolean hasNext() {
				return t < records.length;
			}

			@Override
			public ArchiveFileRecord next() {
                if (t >= records.length) {
                    throw new NoSuchElementException(Integer.toString(t));
                }
			    return records[t++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Archive is read-only");
			}
		};
	}

    @Override
    protected void getFiles(Collection<String> out, String prefix, FileCollectOptions opts)
            throws IOException {

		int index = Arrays.binarySearch(records, prefix, pathComparator);
		if (index < 0) {
			index = -(index+1);
		}

		while (index >= 0 && index < records.length) {
			ArchiveFileRecord record = records[index];
			if (record.getPath().startsWith(prefix)) {
				boolean isFolder = record.isFolder();
				if ((isFolder && opts.collectFolders) || (!isFolder && opts.collectFiles)) {
					String path = record.getPath();
					int slashIndex = path.indexOf('/', prefix.length());
					if (opts.recursive || slashIndex < 0 || slashIndex == path.length()-1) {
						out.add(path);
					}
				}
			} else {
				break; //We're past the subrange that matches the prefix
			}
			index++;
		}
	}

	/**
	 * @return The backing File object if one exists.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return The backing IRandomAccessFile object used by this ZipArchive
	 */
	public IRandomAccessFile getRandomAccessFile() {
		return rfile;
	}

    private static class RecordPathComparator implements Comparator<Object>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(Object a, Object b) {
            if (a == b) {
                return 0;
            }
            return getPath(a).compareTo(getPath(b));
        }

        private static String getPath(Object obj) {
            if (obj instanceof ArchiveFileRecord) {
                return ((ArchiveFileRecord)obj).getPath();
            }
            return String.valueOf(obj);
        }

    }

}
