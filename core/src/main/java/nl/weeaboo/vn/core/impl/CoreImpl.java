package nl.weeaboo.vn.core.impl;

import nl.weeaboo.io.Filenames;

final class CoreImpl {

    static final long serialVersionUID = 54L;

    private CoreImpl() {
    }

    public static String replaceExt(String filename, String ext) {
        int index = filename.indexOf('#');
        if (index < 0) {
            return Filenames.replaceExt(filename, ext);
        }
        return Filenames.replaceExt(filename.substring(0, index), ext) + filename.substring(index);
    }

}
