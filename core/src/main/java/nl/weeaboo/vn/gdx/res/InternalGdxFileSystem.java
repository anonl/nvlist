package nl.weeaboo.vn.gdx.res;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.common.Checks;

/**
 * File system which resolves files using {@link Files#internal}.
 */
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
