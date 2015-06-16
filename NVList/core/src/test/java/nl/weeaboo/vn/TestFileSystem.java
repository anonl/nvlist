package nl.weeaboo.vn;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.filesystem.MultiFileSystem;
import nl.weeaboo.gdx.HeadlessGdx;
import nl.weeaboo.gdx.res.GdxFileSystem;

public final class TestFileSystem {

    static {
        HeadlessGdx.init();
    }
    
    public static IFileSystem newInstance() {
        IFileSystem readFileSystem = new GdxFileSystem("", true);
        IFileSystem inMemoryFileSystem = new InMemoryFileSystem(false);
        return new MultiFileSystem(readFileSystem, inMemoryFileSystem);
    }

}
