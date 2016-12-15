package nl.weeaboo.vn.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.gdx.res.GdxFileSystem;

public class AndroidGdxFileSystem extends GdxFileSystem {

    private static final FileHandle[] NO_FILES = {};

    public AndroidGdxFileSystem() {
        super("", true);
    }

/*
    @Override
    protected boolean exists(FileHandle file) {
        return getMetaData(file) != null;
    }

    @Override
    protected boolean isDirectory(FileHandle file) {
        FileMeta metaData = getMetaData(file);
        return metaData != null && metaData.isFolder();
    }

    @Override
    protected FileHandle[] list(FileHandle file) {
        FileMeta metaData = getMetaData(file);
        if (metaData == null) {
            return NO_FILES;
        }

        List<String> fileNames = metaData.getSubNames();

        FileHandle[] result = new FileHandle[fileNames.size()];
        for (int n = 0; n < result.length; n++) {
            result[n] = resolve(file.path() + "/" + fileNames.get(n));
        }
        return result;
    }
*/
    @Override
    public FileHandle resolve(String fileName) {
        // TODO #32: AndroidFileHandle is really slow for some operations (exists, isDirectory, list)
        return Gdx.files.internal(fileName);
    }

}
