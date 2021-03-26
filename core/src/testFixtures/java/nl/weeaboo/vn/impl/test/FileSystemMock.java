package nl.weeaboo.vn.impl.test;

import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.vn.gdx.res.InternalGdxFileSystem;

public final class FileSystemMock {

    /** Creates a new dummy filesystem to use in unit tests */
    public static MultiFileSystem newInstance() {
        return new MultiFileSystem(newGdxFileSystem(), new InMemoryFileSystem(false));
    }

    /** Creates a GDX file system to use in unit tests */
    public static InternalGdxFileSystem newGdxFileSystem() {
        return new InternalGdxFileSystem("src/test/resources/", "src/test/lua/");
    }

}
