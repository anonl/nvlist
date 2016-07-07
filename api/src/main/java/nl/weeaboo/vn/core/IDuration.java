package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IDuration extends Serializable {

    long toSeconds();

    /** Converts the duration to a human-readable string */
    @Override
    String toString();

}
