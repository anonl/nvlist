package nl.weeaboo.gdx.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class DesktopGdxFileSystem extends GdxFileSystem {

    public DesktopGdxFileSystem(String prefix, boolean isReadOnly) {
        super(prefix, isReadOnly);
    }

    @Override
    public FileHandle resolve(String path) {
        // TODO #33: Don't just resolve files from the res/ folder, also check .nvl archive files
        // Return a virtual file handle that call back into this filesystem for all of its functions.
        return Gdx.files.internal(prefix + path);
    }

}