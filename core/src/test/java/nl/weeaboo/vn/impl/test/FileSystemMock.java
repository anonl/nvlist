package nl.weeaboo.vn.impl.test;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.res.InternalGdxFileSystem;

public final class FileSystemMock {

    static {
        HeadlessGdx.init();
    }

    /** Creates a new dummy filesystem to use in unit tests */
    public static MultiFileSystem newInstance() {
        IFileSystem resourcesFileSystem = newGdxFileSystem();
        IFileSystem luaFileSystem = new InternalGdxFileSystem("src/test/lua/");
        IFileSystem inMemoryFileSystem = new InMemoryFileSystem(false);
        return new MultiFileSystem(resourcesFileSystem, luaFileSystem, inMemoryFileSystem);
    }

    /** Creates a GDX file system to use in unit tests */
    public static InternalGdxFileSystem newGdxFileSystem() {
        return new InternalGdxFileSystem("src/test/resources/");
    }

}
