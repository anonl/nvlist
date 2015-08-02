package nl.weeaboo.styledtext;

public final class MirrorChars {

    private MirrorChars() {
    }

    /**
     * Finds the mirrored equivalent of the given character (where possible). Ideally, the normal character
     * should be just be rendered in reverse, but this requires cooperation from the kerning algorithm (since
     * kerning should be based on the mirrored version of the glyph).
     * <p>
     * See also: http://www.unicode.org/Public/8.0.0/ucd/extracted/DerivedBinaryProperties.txt
     *
     * @return The mirrored equivalent of the given character. Returns the input value if no mirrored version
     *         is known.
     */
    public static char getMirrorChar(char c) {
        // TODO Use a far more complete lookup table. Maybe use ftp://ftp.unicode.org/Public/UNIDATA/BidiMirroring.txt

        switch (c) {
        case '(':
            return ')';
        case ')':
            return '(';
        }
        return c;
    }

}
