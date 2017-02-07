package nl.weeaboo.vn.impl.test;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.res.InternalGdxFileSystem;

public final class TestFileSystem {

    static {
        HeadlessGdx.init();
    }

    /** Creates a new dummy filesystem to use in unit tests */
    public static MultiFileSystem newInstance() {
        IFileSystem readFileSystem = new InternalGdxFileSystem("");
        IFileSystem inMemoryFileSystem = new InMemoryFileSystem(false);
        return new MultiFileSystem(readFileSystem, inMemoryFileSystem);
    }

}
