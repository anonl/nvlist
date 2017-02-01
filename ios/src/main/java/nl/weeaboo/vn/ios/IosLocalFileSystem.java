package nl.weeaboo.vn.ios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.vn.gdx.res.WritableGdxFileSystem;

public final class IosLocalFileSystem extends WritableGdxFileSystem {

    @Override
    public FileHandle resolve(String fileName) {
        return Gdx.files.local(fileName);
    }

}
