package nl.weeaboo.vn.gdx.res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.common.Checks;

public final class InternalGdxFileSystem extends GdxFileSystem {

    private final String prefix;

    public InternalGdxFileSystem(String prefix) {
        super(true);

        this.prefix = Checks.checkNotNull(prefix);
    }

    @Override
    public FileHandle resolve(String fileName) {
        return Gdx.files.internal(prefix + fileName);
    }

}
