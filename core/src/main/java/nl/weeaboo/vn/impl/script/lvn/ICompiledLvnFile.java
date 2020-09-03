package nl.weeaboo.vn.impl.script.lvn;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.filesystem.FilePath;

/**
 * Represents a .lvn file.
 */
public interface ICompiledLvnFile {

    /**
     * @return The filename of the LVN source file.
     */
    FilePath getFilename();

    /**
     * @return The compiled version of the LVN file.
     */
    String getCompiledContents();

    /**
     * @return The compiled contents, annotated by the parser.
     */
    ImmutableList<LvnLine> getLines();

    /**
     * Counts the number of text-mode lines in the compiled LVN file.
     * @param countEmptyLines If {@code true}, count lines that are empty or consist entirely of whitespace.
     */
    int countTextLines(boolean countEmptyLines);

    /**
     * Counts the number of words in text-mode lines in the compiled LVN file.
     */
    int countTextWords();

}
