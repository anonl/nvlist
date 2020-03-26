package nl.weeaboo.vn.buildtools.file;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.Sets;

/**
 * Default implementation of {@link ITempFileProvider}.
 */
@ThreadSafe
public final class TempFileProvider implements ITempFileProvider {

    private final File tempFolder;
    private final Set<File> tempFiles = Sets.newHashSet();

    public TempFileProvider(File tempFolder) {
        this.tempFolder = tempFolder;
    }

    @Override
    public synchronized void deleteAll() {
        for (Iterator<File> itr = tempFiles.iterator(); itr.hasNext(); ) {
            File file = itr.next();
            if (file.delete()) {
                itr.remove();
            }
        }
    }

    @Override
    public synchronized File newTempFile() throws IOException {
        File file = File.createTempFile("nvlist", ".tmp", tempFolder);
        tempFiles.add(file);
        return file;
    }

}
