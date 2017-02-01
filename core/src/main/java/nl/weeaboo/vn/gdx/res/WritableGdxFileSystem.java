package nl.weeaboo.vn.gdx.res;

import java.io.IOException;
import java.io.OutputStream;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IWritableFileSystem;

public abstract class WritableGdxFileSystem extends GdxFileSystem implements IWritableFileSystem {

    public WritableGdxFileSystem() {
        super(false);
    }

    @Override
    public void delete(FilePath path) throws IOException {
        resolveExisting(path).delete();
    }

    @Override
    public void rename(FilePath src, FilePath dst) throws IOException {
        resolveExisting(src).moveTo(resolve(dst));
    }

    @Override
    public void copy(FilePath src, FilePath dst) throws IOException {
        resolveExisting(src).copyTo(resolve(dst));
    }

    @Override
    public OutputStream openOutputStream(FilePath path, boolean append) throws IOException {
        return resolve(path).write(append);
    }

}
