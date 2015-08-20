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

    public static int packRGBAtoARGB(double r, double g, double b, double a) {
        int ri = Math.max(0, Math.min(255, (int) Math.round(r * 255f)));
        int gi = Math.max(0, Math.min(255, (int) Math.round(g * 255f)));
        int bi = Math.max(0, Math.min(255, (int) Math.round(b * 255f)));
        int ai = Math.max(0, Math.min(255, (int) Math.round(a * 255f)));
        return (ai << 24) | (ri << 16) | (gi << 8) | (bi);
    }

}
