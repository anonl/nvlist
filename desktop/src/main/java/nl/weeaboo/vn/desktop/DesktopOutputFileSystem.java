package nl.weeaboo.vn.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.gdx.res.WritableGdxFileSystem;

public final class DesktopOutputFileSystem extends WritableGdxFileSystem {

    private final FileType fileType;
    private final String prefix;

    public DesktopOutputFileSystem(FileType fileType, String prefix) {
        this.fileType = Checks.checkNotNull(fileType);
        this.prefix = Checks.checkNotNull(prefix);
    }

    @Override
    public FileHandle resolve(String fileName) {
        return Gdx.files.getFileHandle(prefix + fileName, fileType);
    }

}