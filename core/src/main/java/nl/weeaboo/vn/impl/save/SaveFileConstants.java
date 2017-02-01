package nl.weeaboo.vn.impl.save;

import nl.weeaboo.filesystem.FilePath;

final class SaveFileConstants {

    public static final FilePath HEADER_PATH = FilePath.of("header.json");
    public static final FilePath SAVEDATA_PATH = FilePath.of("data.bin");
    public static final int FORMAT_VERSION = 1;

    private SaveFileConstants() {
    }

}
