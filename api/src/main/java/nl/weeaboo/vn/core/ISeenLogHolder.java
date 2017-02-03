package nl.weeaboo.vn.core;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.SecureFileWriter;

public interface ISeenLogHolder extends Serializable {

    IResourceSeenLog getResourceLog();

    IChoiceSeenLog getChoiceLog();

    IScriptSeenLog getScriptLog();

    void load(SecureFileWriter sfw, FilePath path) throws IOException;

    void save(SecureFileWriter sfw, FilePath path) throws IOException;

}
